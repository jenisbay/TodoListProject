package com.example.todolistproject.ui.fragments

import android.annotation.SuppressLint
import android.app.Dialog
import android.content.DialogInterface
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.todolistproject.R
import com.example.todolistproject.adapters.CategoryListAdapter
import com.example.todolistproject.databinding.FragmentCategoryListBinding
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.ui.MainActivity
import com.example.todolistproject.ui.TodoViewModel
import com.google.android.material.dialog.MaterialAlertDialogBuilder


class CategoryListFragment : Fragment() {

    lateinit var binding: FragmentCategoryListBinding
    lateinit var viewModel: TodoViewModel
    lateinit var categoryListAdapter: CategoryListAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentCategoryListBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        return binding.root
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupRecyclerView()
        setListeners()
        loadAllCategories()
    }

    private fun setListeners(){

        categoryListAdapter.setOnEditActionClickListener(listener = {category ->
            buildAlertDialogToEditCategory(category)
        })

        categoryListAdapter.setOnDeleteActionClickListener(listener = {category ->
            buildAlertDialogToDeleteCategory(category)
        })

        binding.categoryListToolBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }

        binding.categoryListToolBar.setOnMenuItemClickListener {
            when(it.itemId){
                R.id.menuItemActionAddCategory -> {
                    buildAlertDialogToAddCateogry()
                    true
                }
                else -> false
            }
        }
    }

    private fun deleteCategory(category: Category){
        viewModel.deleteCategory(category)
    }

    private fun editCategory(category: Category){
        viewModel.updateCategory(category)
    }

    private fun addCategory(category: Category){
        viewModel.insertCategory(category)
    }

    private fun buildAlertDialogToAddCateogry(){
        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_new_category_add, null)

        val builder = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .show()

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            builder.dismiss()
        }

        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val categoryName = view.findViewById<EditText>(R.id.etNewCategoryName).text.toString()
            if (categoryName.isNotEmpty()) {
                val category = Category(categoryName)
                addCategory(category)
                builder.dismiss()
            }
        }
    }

    private fun buildAlertDialogToDeleteCategory(category: Category){

        val dialog = MaterialAlertDialogBuilder(requireContext())
            .setTitle("Are you sure?")
            .setMessage("All tasks from the list will also be deleted")
            .setPositiveButton("Delete") { dialog, _ ->
                deleteCategory(category)
                dialog?.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                editCategory(category)
                dialog?.dismiss()
            }
        dialog.show()
    }

    @SuppressLint("MissingInflatedId")
    private fun buildAlertDialogToEditCategory(category: Category){

        val view =
            LayoutInflater.from(requireContext()).inflate(R.layout.layout_new_category_add, null)
        view.findViewById<TextView>(R.id.tvDialogTitle).text = "Edit List"
        val builder = MaterialAlertDialogBuilder(requireContext())
            .setView(view)
            .show()

        view.findViewById<Button>(R.id.btnCancel).setOnClickListener {
            builder.dismiss()
        }

        val etCategoryName = view.findViewById<EditText>(R.id.etNewCategoryName)
        etCategoryName.setText(category.title)

        view.findViewById<Button>(R.id.btnAdd).setOnClickListener {
            val categoryName = etCategoryName.text.toString()

            if (categoryName.isNotEmpty()) {
                category.title = categoryName
                editCategory(category)
                loadAllCategories()
                builder.dismiss()
            }
        }

    }

    private fun loadAllCategories(){
        viewModel.categories.observe(viewLifecycleOwner){
            categoryListAdapter.setCategories(it)
        }
    }

    private fun setupRecyclerView(){
        categoryListAdapter = CategoryListAdapter()
        binding.rvCategoryList.apply {
            adapter = categoryListAdapter
            layoutManager = LinearLayoutManager(requireContext())
        }
    }

}