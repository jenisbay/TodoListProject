package com.example.todolistproject.repositories

import androidx.lifecycle.LiveData
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import com.example.todolistproject.db.TodoListDao
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.CategoryAndTodo
import com.example.todolistproject.db.entities.ToDo

class Repository(
    private val dao: TodoListDao
) {

    // Request for ToDo

    fun getAllTodos() = dao.getAllTodos()

    fun getTodo(pk: Long) = dao.getTodo(pk)

    suspend fun insertTodo(todo: ToDo) {
        dao.insertTodo(todo)
    }

    suspend fun deleteTodo(todo: ToDo) {
        dao.deleteTodo(todo)
    }

    suspend fun updateTodo(todo: ToDo) {
        dao.updateToDo(todo)
    }

    fun getTodosByDescription(query: String): LiveData<List<ToDo>>{
        return dao.getTodosByDescription(query)
    }

    fun getFinishedTodos() = dao.getFinishedTodos()


    // Request for Category

    fun getAllCategories() = dao.getAllCategories()

    fun getCategory(title: String) = dao.getCategory(title)

    suspend fun insertCategory(category: Category) {
        dao.insertCategory(category)
    }

    suspend fun deleteCategory(category: Category) {
        dao.deleteCategory(category)
    }

    suspend fun updateCategory(category: Category) {
        dao.updateCategory(category)
    }

    // Requests to retrieve ToDos by Category

    fun geCategoryById(pk: Long) = dao.getCategoryById(pk)

    fun getToDosByCategory(title: String): LiveData<CategoryAndTodo> {
        return dao.getToDosByCategory(title)
    }


}