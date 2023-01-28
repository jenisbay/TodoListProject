package com.example.todolistproject.ui.fragments

import android.annotation.SuppressLint
import android.app.AlarmManager
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.navigation.fragment.findNavController
import com.example.todolistproject.R
import com.example.todolistproject.databinding.FragmentAddTodoBinding
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.ToDo
import com.example.todolistproject.ui.MainActivity
import com.example.todolistproject.ui.TodoAlarmReceiver
import com.example.todolistproject.ui.TodoViewModel
import com.example.todolistproject.ui.utils.Constants.EXTRA_TODO_DESCRIPTION
import com.example.todolistproject.ui.utils.Constants.EXTRA_TODO_PK

import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.*

private const val TAG = "ADD_FRAGMENT"

class AddTodoFragment : Fragment() {

    lateinit var binding: FragmentAddTodoBinding
    lateinit var viewModel: TodoViewModel
    lateinit var categoryAdapter: ArrayAdapter<String>
    lateinit var alarmManager: AlarmManager
    private val calendar = Calendar.getInstance()
    lateinit var pendingIntent: PendingIntent

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewModel = (activity as MainActivity).viewModel
        binding = FragmentAddTodoBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // setup all onClickListeners
        setOnClickListeners()


        viewModel.categories.observe(viewLifecycleOwner) {
            val categories = mutableListOf<String>()
            it.forEach { category ->
                categories.add(category.title)
            }
            categoryAdapter = ArrayAdapter(requireContext(), R.layout.list_item, categories)
            (binding.menu.editText as AutoCompleteTextView).setAdapter(categoryAdapter)

        }
        binding.menu.editText?.setText("Default")

    }

    // Custom methods

    private fun setOnClickListeners() {
        binding.apply {

            fabSaveTodo.setOnClickListener {
                if (isValid()) {
                    val categoryTitle = binding.menu.editText?.text.toString()
                    val description = binding.etDescription.editText?.text.toString()

                    var category: Category? = null
                    viewModel.categories.value?.forEach {
                        if (it.title == categoryTitle){
                            category = it
                        }
                    }

                    val todo = ToDo(description, calendar.timeInMillis, category!!.pk)
                    saveTodo(todo)
                    setAlarm(todo)
                    findNavController().navigateUp()
                } else {
                    Toast.makeText(requireContext(), "Fill all the fields", Toast.LENGTH_LONG)
                        .show()
                }
            }

            btnDueDate.setOnClickListener {
                val datePicker = buildDatePicker()
                setDatePickerListeners(datePicker)
                activity?.supportFragmentManager?.let { datePicker.show(it, "tag") }
            }

            btnDueTime.setOnClickListener {
                val timePicker = buildTimePicker()
                setTimePickerListeners(timePicker)
                activity?.supportFragmentManager?.let { timePicker.show(it, "tag") }
            }

            btnAddNewCategory.setOnClickListener {
                buildAlertDialog()
            }

            etDueDate.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val datePicker = buildDatePicker()
                    setDatePickerListeners(datePicker)
                    activity?.supportFragmentManager?.let { datePicker.show(it, "tag") }
                }
            }

            etDueTime.editText?.setOnFocusChangeListener { v, hasFocus ->
                if (hasFocus) {
                    val timePicker = buildTimePicker()
                    setTimePickerListeners(timePicker)
                    activity?.supportFragmentManager?.let { timePicker.show(it, "tag") }
                }
            }

            topAppBarAdd.setNavigationOnClickListener {
                findNavController().navigateUp()
            }
        }
    }

    private fun isValid(): Boolean {
        if (TextUtils.isEmpty(binding.etDescription.editText?.text)) return false
        if (TextUtils.isEmpty(binding.etDueDate.editText?.text)) return false
        if (TextUtils.isEmpty(binding.etDueTime.editText?.text)) return false
        return true
    }

    private fun saveTodo(todo: ToDo) {
        viewModel.insertTodo(todo)
    }

    private fun setAlarm(todo: ToDo) {
        alarmManager = requireContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
        val intent = Intent(requireContext(), TodoAlarmReceiver::class.java).apply {
            putExtra(EXTRA_TODO_DESCRIPTION, todo.description)
        }
        pendingIntent = PendingIntent.getBroadcast(requireContext(), todo.pk.toInt(), intent, PendingIntent.FLAG_IMMUTABLE)
        alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)

    }

    private fun buildDatePicker() = MaterialDatePicker
        .Builder
        .datePicker()
        .setTitleText("Select Date")
        .setSelection(MaterialDatePicker.todayInUtcMilliseconds())
        .build()

    private fun buildTimePicker() = MaterialTimePicker
        .Builder()
        .setTimeFormat(TimeFormat.CLOCK_24H)
        .setHour(12)
        .setMinute(30)
        .setTitleText("Select Time")
        .build()

    @SuppressLint("SimpleDateFormat")
    private fun setDatePickerListeners(datePicker: MaterialDatePicker<Long>) {
        datePicker.addOnPositiveButtonClickListener {
            calendar.timeInMillis = it
            val dueDateString =
                SimpleDateFormat("dd/MM/yyyy").format(calendar.timeInMillis)
            binding.etDueDate.editText?.setText(dueDateString)

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
                val dueTimeString = SimpleDateFormat("HH:mm").format(calendar.timeInMillis)
                binding.etDueTime.editText?.setText(dueTimeString)
            }
        }
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