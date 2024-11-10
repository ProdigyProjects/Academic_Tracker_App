package com.example.academictrackerapp.imraan.grade

// MarksListActivity.kt


import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R

class MarksListActivity : AppCompatActivity() {
    private lateinit var textViewMarksList: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks_list)

        textViewMarksList = findViewById(R.id.textViewMarksList)

        // Retrieve marks data from the intent
        val marksData = intent.getStringArrayListExtra("marksList")

        // Display marks if available
        if (marksData != null && marksData.isNotEmpty()) {
            val marksDisplay = StringBuilder("Marks:\n")
            for (mark in marksData) {
                marksDisplay.append("$mark\n")
            }
            textViewMarksList.text = marksDisplay.toString()
        } else {
            textViewMarksList.text = "No marks available."
        }
    }
}
