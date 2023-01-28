package com.example.todolistproject.db.entities

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.android.parcel.Parcelize


@Entity(tableName = "todos")
@Parcelize
data class ToDo(
    @PrimaryKey(autoGenerate = true)
    val pk: Long,
    var description: String,
    var dueDateTime: Long,
    var categoryId: Long,
    var isFinished: Boolean
) : Parcelable {
    constructor(
        description: String,
        dueDateTime: Long,
        categoryId: Long,
        isFinished: Boolean = false
    ): this(0, description, dueDateTime, categoryId, isFinished)

    companion object {}
}

