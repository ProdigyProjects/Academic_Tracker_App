package com.example.academictrackerapp.prisca.model.database

data class Task(
    var id: String = "",
    val title: String = "",
    val dueDate: String = "",
    //val reminder: Boolean = false,
    val note: String = "",
    val isFinished: Boolean = false,
    val reminderTime: String? = null
)

