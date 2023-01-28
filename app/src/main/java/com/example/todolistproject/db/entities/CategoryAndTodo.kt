package com.example.todolistproject.db.entities

import androidx.room.Embedded
import androidx.room.Relation


data class CategoryAndTodo(
    @Embedded val category: Category,
    @Relation(
        parentColumn = "pk",
        entityColumn = "categoryId"
    )
    val todos: List<ToDo>
)