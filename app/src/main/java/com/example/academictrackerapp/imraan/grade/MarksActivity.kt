package com.example.academictrackerapp.Imraan.grade

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MarksActivity : AppCompatActivity() {
    private lateinit var editTextSubject: EditText
    private lateinit var editTextMarks: EditText
    private lateinit var buttonSaveMarks: Button
    private lateinit var buttonViewMarks: Button
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks)

        editTextSubject = findViewById(R.id.editTextSubject)
        editTextMarks = findViewById(R.id.editTextMarks)
        buttonSaveMarks = findViewById(R.id.buttonSaveMarks)
        buttonViewMarks = findViewById(R.id.buttonViewMarks)

        buttonSaveMarks.setOnClickListener {
            saveMarksToFirestore()
        }

        buttonViewMarks.setOnClickListener {
            // Start MarksListActivity without passing marksList
            startActivity(Intent(this, MarksListActivity::class.java))
        }
    }

    private fun saveMarksToFirestore() {
        val subject = editTextSubject.text.toString().trim()
        val marks = editTextMarks.text.toString().trim()

        if (subject.isNotEmpty() && marks.isNotEmpty() && userId != null) {
            // Create a data object with userId, subject, and marks
            val marksData = hashMapOf(
                "userId" to userId,
                "subject" to subject,
                "marks" to marks
            )

            // Save to Firestore
            db.collection("marks")
                .add(marksData)
                .addOnSuccessListener {
                    clearInputs()
                    Toast.makeText(this, "Marks saved successfully!", Toast.LENGTH_SHORT).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error saving marks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "Please enter both subject and marks", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        editTextSubject.text.clear()
        editTextMarks.text.clear()
    }
}
