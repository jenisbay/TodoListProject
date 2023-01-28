package com.example.todolistproject.db.entities

import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val pk: Long,
    var title: String
) {
    constructor(title: String) : this(0, title)
}
