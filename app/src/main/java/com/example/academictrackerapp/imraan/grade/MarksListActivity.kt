package com.example.academictrackerapp.Imraan.grade

import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.academictrackerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class MarksListActivity : AppCompatActivity() {
    private lateinit var textViewMarksList: TextView
    private val db = FirebaseFirestore.getInstance()
    private val userId = FirebaseAuth.getInstance().currentUser?.uid

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_marks_list)

        textViewMarksList = findViewById(R.id.textViewMarksList)

        // Load marks for the current user from Firestore
        loadMarksFromFirestore()
    }

    private fun loadMarksFromFirestore() {
        if (userId != null) {
            db.collection("marks")
                .whereEqualTo("userId", userId)
                .get()
                .addOnSuccessListener { result ->
                    val marksDisplayText = StringBuilder()

                    for (document in result) {
                        val subject = document.getString("subject") ?: "Unknown Subject"
                        val marks = document.getString("marks") ?: "N/A"
                        marksDisplayText.append("$subject: $marks\n")
                    }

                    textViewMarksList.text = marksDisplayText.toString()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Error loading marks: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        } else {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
        }
    }
}
