package com.example.academictrackerapp.prisca.model.database

data class Habit(
    var id: String? = null,
    val title: String = "",
    val daysOfWeek: String = "",
    //val reminder: Boolean = false,
    val note: String = "",
    val dueDate: String? = null,  // Optional due date
    val reminderTime: String? = null  // Optional reminder time
)
