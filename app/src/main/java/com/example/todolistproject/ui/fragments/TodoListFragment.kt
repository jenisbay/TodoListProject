package com.example.todolistproject.ui.fragments

import android.content.res.ColorStateList
import android.os.Bundle
import android.text.TextUtils
import android.view.*
import androidx.fragment.app.Fragment
import androidx.appcompat.widget.PopupMenu
import androidx.appcompat.widget.SearchView
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.R
import com.example.todolistproject.adapters.ToDoListAdapter
import com.example.todolistproject.databinding.FragmentHomeBinding
import com.example.todolistproject.db.entities.ToDo
import com.example.todolistproject.ui.MainActivity
import com.example.todolistproject.ui.TodoViewModel
import com.google.android.material.card.MaterialCardView


class TodoListFragment : Fragment() {

    lateinit var binding: FragmentHomeBinding
    lateinit var viewModel: TodoViewModel
    lateinit var toDoListAdapter: ToDoListAdapter


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentHomeBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()

        setOnClickListeners()
        getAllTodos()
    }

    private fun getAllTodos(){
        viewModel.todos.observe(viewLifecycleOwner) {
            toDoListAdapter.setTodos(it)
            showOrHideEmptyListText(it)
        }
    }

    private fun showEmptyListText(){
        binding.tvEmptyList.visibility = View.VISIBLE
    }

    private fun hideEmptyListText(){
        binding.tvEmptyList.visibility = View.GONE
    }

    private fun showOrHideEmptyListText(todos: List<ToDo>){
        if(todos.isEmpty()){
            showEmptyListText()
        }else{
            hideEmptyListText()
        }
    }

    private fun setOnClickListeners() {

        binding.topAppBar.setNavigationOnClickListener {
                val popup = PopupMenu(requireContext(), binding.appBarLayout)
                popup.menu.add("All")
                viewModel.categories.observe(viewLifecycleOwner){
                     it.forEach { category ->
                        popup.menu.add(category.title)
                    }
                }
                popup.menu.add("Finished")
                popup.setOnMenuItemClickListener {
                    when(it.title){
                        "All" -> {
                            getAllTodos()
                            binding.topAppBar.title = "All"
                            true
                        }
                        "Finished" -> {
                            viewModel.getFinishedTodos().observe(viewLifecycleOwner){
                                toDoListAdapter.setTodos(it.toList())
                                showOrHideEmptyListText(it)
                            }
                            binding.topAppBar.title = "Finished"
                            true
                        }
                        else -> {
                            viewModel.getTodosByCategory(it.title.toString()).observe(viewLifecycleOwner){
                                toDoListAdapter.setTodos(it.todos.toList())
                                showOrHideEmptyListText(it.todos)
                            }
                            binding.topAppBar.title = it.title
                            true
                        }
                    }
                }
                popup.show()
        }

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_addTodoFragment)
        }

        toDoListAdapter.setOnItemLongClickListener(listener = { todo, view ->
            if (!selection.isActionMode) {
                selection.startActionMode()
                selection.addSelectedItem(todo, view)
            }
        })

        toDoListAdapter.setOnItemClickListener(listener = { todo, view ->
            if (selection.isActionMode && !view.isChecked) {
                selection.addSelectedItem(todo, view)
            } else if (selection.isActionMode && view.isChecked) {
                selection.removeSelectedItem(todo, view)
            } else {
                val action = TodoListFragmentDirections.actionHomeFragmentToDetailTodoFragment(todo)
                findNavController().navigate(action)
            }
        })

        binding.searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                filterQuery(query)
                return true
            }

            override fun onQueryTextChange(query: String?): Boolean {
                filterQuery(query)
                return true
            }
        })

        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.categoryList -> {
                    findNavController().navigate(R.id.action_homeFragment_to_categoryListFragment)
                    true
                }
                else -> false
            }
        }

    }

    private fun filterQuery(query: String?) {
        query?.let {
            viewModel.getTodosByDescription(it).observe(viewLifecycleOwner) { todos ->
                toDoListAdapter.setTodos(todos)
                showOrHideEmptyListText(todos)
            }
        }
        if (TextUtils.isEmpty(query)) {
            getAllTodos()
        }
    }

    private fun setupRecyclerView() {
        toDoListAdapter = ToDoListAdapter(requireContext())
        binding.rvTodos.apply {
            adapter = toDoListAdapter
            val linearLayoutManager = LinearLayoutManager(context)
            linearLayoutManager.reverseLayout = true
            linearLayoutManager.stackFromEnd = true
            layoutManager = linearLayoutManager
        }
    }

    private val selection = object : Any() {

        var isActionMode = false
            private set

        var actionMode: ActionMode? = null
            private set

        var counter = 0
            private set

        var selectedTodoList: MutableList<ToDo> = mutableListOf()
            private set

        var selectedViewList: MutableList<MaterialCardView> = mutableListOf()
            private set

        fun startActionMode() {
            if (actionMode == null) {
                actionMode = activity?.startActionMode(actionModeCallback)
                onStartActionMode()
            }
        }

        fun onStartActionMode() {
            isActionMode = true
        }

        fun destroyActionMode() {
            actionMode?.let {
                it.finish()
                onDestroyActionMode()
            }
        }

        fun onDestroyActionMode() {
            actionMode = null
            isActionMode = false
        }

        fun addSelectedItem(todo: ToDo, view: MaterialCardView) {
            checkSelectedItem(view)
            selectedTodoList.add(todo)
            selectedViewList.add(view)
            increaseCounterByOne()
            changeActionModeTitle()
        }

        fun removeSelectedItem(todo: ToDo, view: MaterialCardView) {
            uncheckSelectedItem(view)
            selectedViewList.remove(view)
            selectedTodoList.remove(todo)
            decreaseCounterByOne()
            changeActionModeTitle()
        }

        fun checkSelectedItem(view: MaterialCardView) {
            view.isChecked = true
            view.checkedIconTint = ColorStateList.valueOf(resources.getColor(R.color.white))
        }

        fun uncheckSelectedItem(view: MaterialCardView) {
            view.isChecked = false
        }

        fun uncheckSelectedItems() {
            selectedViewList.forEach { view ->
                uncheckSelectedItem(view)
            }
        }

        fun clearSelectionItems() {
            uncheckSelectedItems()
            selectedViewList.clear()
            selectedTodoList.clear()
            annulCounter()
        }

        fun annulCounter() {
            counter = 0
        }

        fun increaseCounterByOne() {
            counter++
        }

        fun decreaseCounterByOne() {
            counter--
        }

        private fun changeActionModeTitle() {
            if (counter <= 1) {
                actionMode?.title = "$counter item selected"
            } else {
                actionMode?.title = "$counter items selected"
            }

        }

        private val actionModeCallback = object : ActionMode.Callback {

            override fun onCreateActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                MenuInflater(requireContext()).inflate(R.menu.contextual_action_bar, menu)
                return true
            }

            override fun onPrepareActionMode(mode: ActionMode?, menu: Menu?): Boolean {
                return false
            }

            override fun onActionItemClicked(mode: ActionMode?, item: MenuItem?): Boolean {
                return when (item?.itemId) {
                    R.id.delete -> {
                        selectedTodoList.forEach { todo ->
                            viewModel.deleteTodo(todo)
                        }
                        clearSelectionItems()
                        destroyActionMode()
                        true
                    }
                    R.id.finished -> {
                        selectedTodoList.forEach { todo ->
                            todo.isFinished = true
                            viewModel.updateTodo(todo)
                        }
                        clearSelectionItems()
                        destroyActionMode()
                        true
                    }
                    else -> false
                }
            }

            override fun onDestroyActionMode(mode: ActionMode?) {
                clearSelectionItems()
                onDestroyActionMode()
            }
        }


    }

}