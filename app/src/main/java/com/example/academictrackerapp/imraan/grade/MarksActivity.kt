package com.example.academictrackerapp.imraan.grade

// MarksActivity.kt


import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R

class MarksActivity : AppCompatActivity() {
    private lateinit var editTextSubject: EditText
    private lateinit var editTextMarks: EditText
    private lateinit var buttonSaveMarks: Button
    private lateinit var buttonViewMarks: Button
    private val marksList = mutableListOf<String>() // To store subject and marks

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks)

        editTextSubject = findViewById(R.id.editTextSubject)
        editTextMarks = findViewById(R.id.editTextMarks)
        buttonSaveMarks = findViewById(R.id.buttonSaveMarks)
        buttonViewMarks = findViewById(R.id.buttonViewMarks) // New button to view marks

        buttonSaveMarks.setOnClickListener {
            saveMarks()
        }

        buttonViewMarks.setOnClickListener {
            viewMarks()
        }
    }

    private fun saveMarks() {
        val subject = editTextSubject.text.toString().trim()
        val marks = editTextMarks.text.toString().trim()

        if (subject.isNotEmpty() && marks.isNotEmpty()) {
            // Add to marks list
            marksList.add("$subject: $marks")
            clearInputs()
            Toast.makeText(this, "Marks saved successfully!", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(this, "Please enter both subject and marks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun viewMarks() {
        val intent = Intent(this, MarksListActivity::class.java)
        intent.putStringArrayListExtra("marksList", ArrayList(marksList))
        startActivity(intent)
    }

    private fun clearInputs() {
        editTextSubject.text.clear()
        editTextMarks.text.clear()
    }
}
