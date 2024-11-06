package com.example.academictrackerapp.Imraan.TimeTable

import android.app.Activity
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.Spinner
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R // Ensure this import works
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import yuku.ambilwarna.AmbilWarnaDialog // Import for AmbilWarna

class TaskCreationActivity : AppCompatActivity() {
    private lateinit var taskName: EditText
    private lateinit var daySpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private var selectedColor = Color.RED // Default selected color
    private lateinit var colorButton: Button // Button to select color

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_creation) // Ensure this layout exists

        // Initialize views with the correct IDs from your XML layout
        taskName = findViewById(R.id.task_name) // Matches the EditText ID in XML
        daySpinner = findViewById(R.id.day_spinner) // Matches the Spinner ID for day selection
        timeSpinner = findViewById(R.id.time_spinner) // Matches the Spinner ID for time selection
        val submitTaskButton: Button = findViewById(R.id.submit_task_button) // Matches the Submit button ID
        colorButton = findViewById(R.id.color_button) // Matches the Color button ID

        // Set up the day spinner
        val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        daySpinner.adapter = dayAdapter

        // Set up the time spinner
        val times = arrayOf("08:00", "08:30", "09:00", "09:30", "10:00") // Add more times as needed
        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, times)
        timeSpinner.adapter = timeAdapter

        // Set color button click listener
        colorButton.setOnClickListener {
            showColorPickerDialog()
        }

        // Set up the submit button listener
        submitTaskButton.setOnClickListener {
            val task = taskName.text.toString()
            val day = daySpinner.selectedItem.toString()
            val time = timeSpinner.selectedItem.toString()

            if (task.isNotEmpty()) {
                saveTaskToFirebase(task, day, time, selectedColor) // Save task to Firebase
            } else {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun showColorPickerDialog() {
        val colorPickerDialog = AmbilWarnaDialog(this, selectedColor, object : AmbilWarnaDialog.OnAmbilWarnaListener {
            override fun onOk(dialog: AmbilWarnaDialog?, color: Int) {
                selectedColor = color
                // Update the button's background color to show the selected color
                colorButton.setBackgroundColor(selectedColor)
            }

            override fun onCancel(dialog: AmbilWarnaDialog?) {
                // Handle cancel action if needed
            }
        })
        colorPickerDialog.show()
    }

    private fun saveTaskToFirebase(task: String, day: String, time: String, color: Int) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid // Get the current user's ID
        if (userId != null) { // Ensure the user is authenticated
            val db = FirebaseFirestore.getInstance()
            val taskData = hashMapOf(
                "task" to task,
                "day" to day,
                "time" to time,
                "color" to color
            )

            // Save the task under the user's document
            db.collection("users") // Use the users collection
                .document(userId) // User-specific document
                .collection("tasks") // Tasks subcollection
                .add(taskData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Task added successfully", Toast.LENGTH_SHORT).show()
                    val resultIntent = Intent()
                    resultIntent.putExtra("task", task)
                    resultIntent.putExtra("day", day)
                    resultIntent.putExtra("time", time)
                    resultIntent.putExtra("color", color)
                    setResult(Activity.RESULT_OK, resultIntent)
                    finish() // This returns to the TimetableActivity
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error adding task: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User is not authenticated", Toast.LENGTH_SHORT).show()
        }
    }
}
