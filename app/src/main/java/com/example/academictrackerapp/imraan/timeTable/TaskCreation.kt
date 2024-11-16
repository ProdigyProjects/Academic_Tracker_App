package com.example.academictrackerapp.Imraan.TimeTable

import android.annotation.SuppressLint
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
import com.example.academictrackerapp.Imraan.BaseActivity
import com.example.academictrackerapp.R
import yuku.ambilwarna.AmbilWarnaDialog // Import for AmbilWarna

class TaskCreationActivity : BaseActivity() {
    private lateinit var taskName: EditText
    private lateinit var daySpinner: Spinner
    private lateinit var timeSpinner: Spinner
    private var selectedColor = Color.RED // Default selected color
    private lateinit var colorButton: Button // Button to select color

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_creation)

        // Initialize views
        taskName = findViewById(R.id.task_name)
        daySpinner = findViewById(R.id.day_spinner) // Spinner for day selection
        timeSpinner = findViewById(R.id.time_spinner) // Spinner for time selection
        val submitTaskButton: Button = findViewById(R.id.submit_task_button)
        colorButton = findViewById(R.id.color_button) // Button for color selection

        // Set up the day
        val days = arrayOf("Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday")
        val dayAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, days)
        daySpinner.adapter = dayAdapter

        // Set up the time
        val times = arrayOf("08:00", "09:00", "10:00", "11:00", "12:00", "13:00", "14:00", "15:00", "16:00", "17:00", "18:00", "19:00","20:00") // Add more times as needed
        val timeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, times)
        timeSpinner.adapter = timeAdapter

        // Set color button click listener
        colorButton.setOnClickListener {
            showColorPickerDialog()
        }

        // Set up the submit button listener
        submitTaskButton.setOnClickListener {
            val task = taskName.text.toString()
            val day = daySpinner.selectedItem?.toString()
            val time = timeSpinner.selectedItem?.toString()

            // Validate task name
            if (task.isEmpty()) {
                Toast.makeText(this, "Please enter a task name", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate day selection
            if (day.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a day", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate time selection
            if (time.isNullOrEmpty()) {
                Toast.makeText(this, "Please select a time", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate color selection (default is RED, check if unchanged)
            if (selectedColor == Color.RED) {
                Toast.makeText(this, "Please select a color", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create result intent and pass data back
            val resultIntent = Intent().apply {
                putExtra("task", task)
                putExtra("day", day)
                putExtra("time", time)
                putExtra("color", selectedColor)
            }

            // Set result and finish activity
            setResult(Activity.RESULT_OK, resultIntent)
            finish()
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
}
