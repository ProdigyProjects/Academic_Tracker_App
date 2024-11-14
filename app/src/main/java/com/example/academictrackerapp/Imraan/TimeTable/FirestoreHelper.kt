package com.example.academictrackerapp.Imraan.TimeTable

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.DocumentSnapshot
import android.util.Log

class FirestoreHelper {

    private val firestore = FirebaseFirestore.getInstance()
    private val auth = FirebaseAuth.getInstance()

    // Save the timetable data to Firestore
    fun saveTimetable(timetable: Timetable, userId: String) {
        val timetableRef = firestore.collection("timetables").document(userId)

        timetableRef.set(timetable)
            .addOnSuccessListener {
                Log.d("FirestoreHelper", "Timetable saved successfully")
            }
            .addOnFailureListener { exception ->
                Log.e("FirestoreHelper", "Error saving timetable", exception)
            }
    }

    // Fetch the timetable from Firestore
    fun fetchTimetable(userId: String, onSuccess: (Timetable?) -> Unit, onFailure: (Exception) -> Unit) {
        val timetableRef = firestore.collection("timetables").document(userId)
        timetableRef.get().addOnSuccessListener { document ->
            if (document != null && document.exists()) {
                val timetable = document.toObject(Timetable::class.java)
                onSuccess(timetable)
            } else {
                onSuccess(null) // Return null if document doesn't exist
            }
        }.addOnFailureListener { exception ->
            onFailure(exception)
        }
    }
}
