package com.example.todolistproject.ui.utils
import android.annotation.SuppressLint
import java.text.SimpleDateFormat
import java.util.Calendar
import kotlin.math.floor


@SuppressLint("SimpleDateFormat")
fun convertMillisecondsIntoTimeString(milliseconds: Long): String{
    return SimpleDateFormat("HH:mm").format(milliseconds).toString()
}

@SuppressLint("SimpleDateFormat")
fun convertMillisecondsIntoDateString(milliseconds: Long): String{
    return SimpleDateFormat("dd/MM/yyyy").format(milliseconds).toString()
}

fun convertMillisecondsIntoDateTimeString(milliseconds: Long): String {
    return SimpleDateFormat("dd/MM/yyyy HH:mm").format(milliseconds).toString()
}