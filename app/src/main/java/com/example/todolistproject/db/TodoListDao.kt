package com.example.todolistproject.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.todolistproject.db.entities.Category
import com.example.todolistproject.db.entities.CategoryAndTodo
import com.example.todolistproject.db.entities.ToDo


@Dao
interface TodoListDao {

    // Requests for ToDo

    @Query("SELECT * FROM todos WHERE isFinished = :isFinished ORDER BY pk ASC")
    fun getAllTodos(isFinished: Boolean = false): LiveData<List<ToDo>>

    @Query("SELECT * FROM todos WHERE pk = :pk")
    fun getTodo(pk: Long): ToDo

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTodo(todo: ToDo)

    @Delete
    suspend fun deleteTodo(todo: ToDo)

    @Update
    suspend fun updateToDo(todo: ToDo)

    @Query("SELECT * FROM todos WHERE description LIKE '%' || :query || '%'")
    fun getTodosByDescription(query: String): LiveData<List<ToDo>>

    @Query("SELECT * FROM todos WHERE isFinished = :isFinished")
    fun getFinishedTodos(isFinished: Boolean = true): LiveData<List<ToDo>>

    // Requests for Category

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: Category)

    @Update
    suspend fun updateCategory(category: Category)

    @Delete
    suspend fun deleteCategory(category: Category)

    @Query("SELECT * FROM categories ORDER BY title ASC")
    fun getAllCategories(): LiveData<List<Category>>

    @Query("SELECT * FROM categories WHERE title = :title")
    fun getCategory(title: String): LiveData<Category>

    @Query("SELECT * FROM categories WHERE pk = :pk")
    fun getCategoryById(pk: Long): LiveData<Category>

    // Requests to retrieve ToDos By Category

    @Transaction
    @Query("SELECT * FROM categories WHERE title = :title")
    fun getToDosByCategory(title: String): LiveData<CategoryAndTodo>
}