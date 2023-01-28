package com.example.todolistproject.ui

import android.app.Application
import android.content.Context
import android.icu.text.CaseMap
import android.util.Log
import androidx.lifecycle.*
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import com.example.todolistproject.R
import com.example.todolistproject.db.TodoListDatabase
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.CategoryAndTodo
import com.example.todolistproject.db.entities.ToDo
import com.example.todolistproject.repositories.Repository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONArray
import org.json.JSONException
import java.io.BufferedReader


class TodoViewModel(application: Application) : AndroidViewModel(application) {

    val todos: LiveData<List<ToDo>>
    val categories: LiveData<List<Category>>

    private val repository: Repository

    init {
        repository = Repository(
            TodoListDatabase.invoke(application).getDao()
        )
        todos = repository.getAllTodos()
        categories = repository.getAllCategories()
    }

    // ToDo Requests

    fun getAllTodos() = repository.getAllTodos()

    fun getFinishedTodos() = repository.getFinishedTodos()

    fun getTodo(pk: Long) = repository.getTodo(pk)

    fun insertTodo(todo: ToDo) {
        viewModelScope.launch {
            repository.insertTodo(todo)
        }
    }

    fun deleteTodo(todo: ToDo) {
        viewModelScope.launch {
            repository.deleteTodo(todo)
        }
    }

    fun updateTodo(todo: ToDo) {
        viewModelScope.launch {
            repository.updateTodo(todo)
        }
    }

    fun getTodosByDescription(query: String): LiveData<List<ToDo>> {
        return repository.getTodosByDescription(query)
    }

    // Category Request

    fun getCategory(title: String) = repository.getCategory(title)

    fun insertCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.insertCategory(category)
        }
    }

    fun updateCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.updateCategory(category)
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch(Dispatchers.IO) {
            repository.deleteCategory(category)
        }
    }

    fun getCategoryById(pk: Long) = repository.geCategoryById(pk)

    fun getTodosByCategory(title: String) = repository.getToDosByCategory(title)


}