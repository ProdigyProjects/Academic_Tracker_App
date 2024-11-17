package com.example.academictrackerapp.prisca.model.repositories

import com.example.academictrackerapp.elvis.data.Reminder
import com.google.firebase.firestore.FirebaseFirestore

class ReminderRepository() {

    val db = FirebaseFirestore.getInstance()

    fun isRemindersCollectionCreated(onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reminders").limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createReminder(reminder: Reminder, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reminders").add(reminder)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun readReminder(reminderId: String, onSuccess: (Reminder) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reminders").document(reminderId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Reminder::class.java)?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateReminder(reminderId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reminders").document(reminderId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteReminder(reminderId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("reminders").document(reminderId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
}
