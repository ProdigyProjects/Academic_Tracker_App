package com.example.academictrackerapp.Imraan.TimeTable

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TableLayout
import android.widget.TableRow
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth

class TimetableActivity : AppCompatActivity() {
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid // Get current user ID
    private lateinit var tableLayout: TableLayout

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_timetable)

        // Initialize views
        val addTaskButton: Button = findViewById(R.id.add_task_button)
        tableLayout = findViewById(R.id.tableLayout)
        val backButton: ImageView = findViewById(R.id.image_back)

        // Set click listener for Add Task button
        addTaskButton.setOnClickListener {
            val intent = Intent(this, TaskCreationActivity::class.java)
            startActivityForResult(intent, REQUEST_CODE)
        }

        // Set click listener for back button
        backButton.setOnClickListener {
            finish() // Finish current activity and return to the previous one
        }

        // Load timetable data
        loadTimetableFromFirestore()
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            data?.let {
                val day = it.getStringExtra("day")
                val time = it.getStringExtra("time")
                val taskName = it.getStringExtra("task")
                val color = it.getIntExtra("color", Color.GRAY)

                if (day != null && time != null && taskName != null) {
                    addTaskToTimetable(day, time, taskName, color)
                    saveTaskToFirestore(day, time, taskName, color) // Save to Firestore
                }
            }
        }
    }

    private fun addTaskToTimetable(day: String, time: String, taskName: String, color: Int) {
        val dayIndex = getDayIndex(day)
        val timeIndex = getTimeIndex(time)

        if (dayIndex != -1 && timeIndex != -1) {
            val row = tableLayout.getChildAt(timeIndex + 1) as TableRow
            val cell = row.getChildAt(dayIndex + 1) as TextView

            cell.text = taskName
            cell.setBackgroundColor(color)
        }
    }

    private fun getDayIndex(day: String): Int {
        return when (day) {
            "Sunday" -> 0
            "Monday" -> 1
            "Tuesday" -> 2
            "Wednesday" -> 3
            "Thursday" -> 4
            "Friday" -> 5
            "Saturday" -> 6
            else -> -1
        }
    }

    private fun getTimeIndex(time: String): Int {
        return when (time) {
            "08:00" -> 0
            "09:00" -> 1
            "10:00" -> 2
            "11:00" -> 3
            "12:00" -> 4
            "13:00" -> 5
            "14:00" -> 6
            "15:00" -> 7
            "16:00" -> 8
            "17:00" -> 9
            "18:00" -> 10
            "19:00" -> 11
            "20:00" -> 12


            else -> -1
        }
    }

    private fun saveTaskToFirestore(day: String, time: String, taskName: String, color: Int) {
        if (userId != null) {
            val taskData = mapOf(
                "task" to taskName,
                "color" to color
            )

            // Reference to the user's timetable document
            val timetableDocRef = db.collection("timetables").document(userId)

            // Check if the document exists
            timetableDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    // Document exists, update the document
                    val updateData = mapOf(
                        "$day.$time" to taskData
                    )
                    timetableDocRef.update(updateData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error saving task: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                } else {
                    // Document doesn't exist, create it with initial data
                    val timetableData = mutableMapOf<String, Any>(
                        day to mapOf(time to taskData)
                    )

                    timetableDocRef.set(timetableData)
                        .addOnSuccessListener {
                            Toast.makeText(this, "Task saved successfully", Toast.LENGTH_SHORT).show()
                        }
                        .addOnFailureListener { exception ->
                            Toast.makeText(this, "Error saving task: ${exception.message}", Toast.LENGTH_SHORT).show()
                        }
                }
            }
        }
    }

    private fun loadTimetableFromFirestore() {
        if (userId != null) {
            val timetableDocRef = db.collection("timetables").document(userId)
            timetableDocRef.get().addOnSuccessListener { document ->
                if (document.exists()) {
                    val timetableData = document.data
                    timetableData?.forEach { (day, times) ->
                        if (times is Map<*, *>) {
                            times.forEach { (time, taskData) ->
                                if (taskData is Map<*, *>) {
                                    val taskName = taskData["task"] as String
                                    val color = taskData["color"] as Long
                                    addTaskToTimetable(day, time.toString(), taskName, color.toInt())
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    companion object {
        private const val REQUEST_CODE = 1
    }
}
