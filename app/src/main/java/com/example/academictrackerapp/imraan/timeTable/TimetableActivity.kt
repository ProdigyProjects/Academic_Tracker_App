package com.example.academictrackerapp.imraan.timeTable

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R
import com.google.firebase.firestore.FirebaseFirestore

class TimetableActivity : AppCompatActivity() {

    private lateinit var tableLayout: TableLayout
    private val db = FirebaseFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)

        tableLayout = findViewById(R.id.tableLayout)

        // Back button functionality
        //findViewById<ImageView>(R.id.image_back).setOnClickListener { finish() }

        // Load tasks from Firestore
        loadTasks()

        // Add Task Button
        findViewById<Button>(R.id.add_task_button).setOnClickListener {
            startActivityForResult(Intent(this, TaskCreationActivity::class.java), 1)
        }
    }

    private fun loadTasks() {
        db.collection("tasks")
            .get()
            .addOnSuccessListener { documents ->
                for (document in documents) {
                    val task = document.getString("task") ?: ""
                    val day = document.getString("day") ?: ""
                    val time = document.getString("time") ?: ""
                    val color = document.getString("color") ?: "#FFFFFF"
                    addTaskToTimetable(task, day, time, color)
                }
            }
    }

    private fun addTaskToTimetable(task: String, day: String, time: String, color: String) {
        val rowIndex = getTimeRowIndex(time)
        val colIndex = getDayColumnIndex(day)

        if (rowIndex >= 0 && colIndex >= 0) {
            val row = tableLayout.getChildAt(rowIndex) as TableRow
            val cell = row.getChildAt(colIndex) as TextView
            cell.text = task
            cell.setBackgroundColor(android.graphics.Color.parseColor(color))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 && resultCode == Activity.RESULT_OK) {
            val task = data?.getStringExtra("task") ?: return
            val day = data.getStringExtra("day") ?: return
            val time = data.getStringExtra("time") ?: return
            val color = data.getStringExtra("color") ?: "#FFFFFF"

            saveTaskToFirebase(task, day, time, color)
            addTaskToTimetable(task, day, time, color)
        }
    }

    private fun saveTaskToFirebase(task: String, day: String, time: String, color: String) {
        val taskData = hashMapOf("task" to task, "day" to day, "time" to time, "color" to color)
        db.collection("tasks").add(taskData)
    }

    private fun getTimeRowIndex(time: String): Int {
        return when (time) {
            "08:00" -> 1
            "08:30" -> 2
            "09:00" -> 3
            else -> -1
        }
    }

    private fun getDayColumnIndex(day: String): Int {
        return when (day) {
            "Sunday" -> 1
            "Monday" -> 2
            "Tuesday" -> 3
            "Wednesday" -> 4
            "Thursday" -> 5
            "Friday" -> 6
            "Saturday" -> 7
            else -> -1
        }
    }
}

