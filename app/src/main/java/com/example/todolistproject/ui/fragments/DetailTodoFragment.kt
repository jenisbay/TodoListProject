package com.example.todolistproject.ui.fragments

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContentProviderCompat.requireContext
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.example.todolistproject.R
import com.example.todolistproject.databinding.FragmentDetailTodoBinding
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.ToDo
import com.example.todolistproject.ui.MainActivity
import com.example.todolistproject.ui.TodoViewModel
import com.example.todolistproject.ui.utils.convertMillisecondsIntoDateString
import com.example.todolistproject.ui.utils.convertMillisecondsIntoTimeString
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import java.text.SimpleDateFormat
import java.util.*


class DetailTodoFragment : Fragment() {

    private val args: DetailTodoFragmentArgs by navArgs()
    lateinit var binding: FragmentDetailTodoBinding
    lateinit var viewModel: TodoViewModel
    lateinit var categoryAdapter: ArrayAdapter<String>
    private val calendar = Calendar.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentDetailTodoBinding.inflate(inflater, container, false)
        viewModel = (activity as MainActivity).viewModel
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val todo = args.todo
        setupListeners(todo)
        fillFields(todo)
    }

    @SuppressLint("SimpleDateFormat")
    private fun fillFields(todo: ToDo) {
        calendar.timeInMillis = todo.dueDateTime
        binding.etDetailDescription.editText?.setText(todo.description)
        binding.etDetailDueDate.editText?.setText(convertMillisecondsIntoDateString(calendar.timeInMillis))
        binding.etDetailDueTime.editText?.setText(convertMillisecondsIntoTimeString(calendar.timeInMillis))
        viewModel.categories.observe(viewLifecycleOwner) {
            val categories = mutableListOf<String>()
            it.forEach { category ->
                categories.add(category.title)
                if (category.pk == todo.categoryId) {
                    binding.menuDetail.editText?.setText(category.title)
                }
            }
            categoryAdapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            (binding.menuDetail.editText as AutoCompleteTextView).setAdapter(categoryAdapter)
        }

    }

    @SuppressLint("SimpleDateFormat")
    private fun setupListeners(todo: ToDo) {

        (binding.menuDetail.editText as AutoCompleteTextView).setOnItemClickListener { parent, view, position, id ->
            val categoryTitle = parent.adapter.getItem(position)
            viewModel.categories.value?.forEach { category ->
                if (category.title == categoryTitle) {
                    todo.categoryId = category.pk
                }
            }
        }

        binding.fabDetailSaveTodo.setOnClickListener {
            todo.description = binding.etDetailDescription.editText?.text.toString()
            todo.dueDateTime = calendar.timeInMillis
            saveTodo(todo)
            findNavController().navigateUp()
        }

        binding.topAppBar.setNavigationOnClickListener {
            findNavController().navigateUp()
        }
        binding.topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.menuItemDetailShare -> {
                    // Share
                    val dueDateString =
                        SimpleDateFormat("dd LLL yyyy").format(todo.dueDateTime).toString()
                    val dueTimeString =
                        SimpleDateFormat("HH:mm").format(todo.dueDateTime).toString()
                    val extraTextValue =
                        "${todo.description} ($dueDateString, $dueTimeString)"
                    val sendIntent = Intent(Intent.ACTION_SEND).apply {
                        putExtra(Intent.EXTRA_TEXT, extraTextValue)
                        type = "text/plain"
                    }
                    val shareIntent = Intent.createChooser(sendIntent, null)
                    startActivity(shareIntent)
                    true
                }
                R.id.menuItemDetailDelete -> {

                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle("Are you sure?")
                        .setNegativeButton("Cancel") { dialog, _ -> dialog?.dismiss() }
                        .setPositiveButton("Delete") { dialog, _ ->
                            deleteTodo(todo)
                            dialog?.dismiss()
                            findNavController().navigateUp()
                        }.show()
                    true
                }
                else -> false
            }
        }

        binding.btnDetailDueTime.setOnClickListener {
            val timePicker = buildTimePicker(calendar.timeInMillis)
            setTimePickerListeners(timePicker)
            activity?.supportFragmentManager?.let { timePicker.show(it, "tag") }
        }

        binding.btnDetailDueDate.setOnClickListener {
            val datePicker = buildDatePicker(calendar.timeInMillis)
            setDatePickerListeners(datePicker)
            activity?.supportFragmentManager?.let { datePicker.show(it, "tag") }
        }

        binding.etDetailDueTime.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val timePicker = buildTimePicker(calendar.timeInMillis)
                setTimePickerListeners(timePicker)
                activity?.supportFragmentManager?.let { timePicker.show(it, "tag") }
            }
        }

        binding.etDetailDueDate.editText?.setOnFocusChangeListener { v, hasFocus ->
            if (hasFocus) {
                val datePicker = buildDatePicker(calendar.timeInMillis)
                setDatePickerListeners(datePicker)
                activity?.supportFragmentManager?.let { datePicker.show(it, "tag") }
            }
        }

        binding.btnDetailAddNewCategory.setOnClickListener {
            buildAlertDialog()
        }
    }

    private fun deleteTodo(todo: ToDo) {
        viewModel.deleteTodo(todo)
    }

    private fun buildDatePicker(milliseconds: Long): MaterialDatePicker<Long> {

        return MaterialDatePicker
            .Builder
            .datePicker()
            .setTitleText("Select Date")
            .setSelection(milliseconds)
            .build()
    }

    private fun buildTimePicker(milliseconds: Long): MaterialTimePicker {

        val hour = calendar[Calendar.HOUR_OF_DAY]
        val minute = calendar[Calendar.MINUTE]

        return MaterialTimePicker
            .Builder()
            .setTimeFormat(TimeFormat.CLOCK_24H)
            .setHour(hour)
            .setMinute(minute)
            .setTitleText("Select Time")
            .build()
    }

    @SuppressLint("SimpleDateFormat")
    private fun setDatePickerListeners(datePicker: MaterialDatePicker<Long>) {
        datePicker.apply {
            addOnPositiveButtonClickListener {
                calendar.timeInMillis = it.toLong()
                val dueDateString = android.icu.text.SimpleDateFormat("dd/MM/yyyy").format(it)
                binding.etDetailDueDate.editText?.setText(dueDateString)
            }
        }
    }

    @SuppressLint("SetTextI18n", "SimpleDateFormat")
    private fun setTimePickerListeners(timePicker: MaterialTimePicker) {
        timePicker.apply {
            addOnPositiveButtonClickListener {
                calendar[Calendar.HOUR_OF_DAY] = hour
                calendar[Calendar.MINUTE] = minute
                calendar[Calendar.SECOND] = 0
                calendar[Calendar.MILLISECOND] = 0
                val dueTimeString =
                    android.icu.text.SimpleDateFormat("HH:mm").format(calendar.timeInMillis)
                binding.etDetailDueTime.editText?.setText(dueTimeString)
            }
        }
    }

    private fun saveTodo(todo: ToDo) {
        viewModel.updateTodo(todo)
    }

    @SuppressLint("MissingInflatedId")
    private fun buildAlertDialog() {

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
                viewModel.insertCategory(category)
                builder.dismiss()
            }
        }
    }

}