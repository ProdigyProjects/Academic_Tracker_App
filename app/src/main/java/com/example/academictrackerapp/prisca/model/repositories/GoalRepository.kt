package com.example.academictrackerapp.prisca.model.repositories

import com.example.academictrackerapp.prisca.model.database.Goal
import com.google.firebase.firestore.FirebaseFirestore

class GoalRepository {
    val db = FirebaseFirestore.getInstance()

    fun isGoalCollectionCreated(onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("goals").limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createGoal(goal: Goal, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        // Add the goal to Firestore
        db.collection("goals").add(goal)
            .addOnSuccessListener { documentReference ->
                // Once the goal is added, we update the goal with the document ID
                goal.id = documentReference.id

                // Now, we update the document with its own ID
                // To update the document, we use the document reference and set the `id` field
                documentReference.set(goal)
                    .addOnSuccessListener {
                        // Call the success callback after successfully updating the document
                        onSuccess()
                    }
                    .addOnFailureListener { exception ->
                        // Call the failure callback if updating the document fails
                        onFailure(exception)
                    }
            }
            .addOnFailureListener { exception ->
                // Handle failure in adding the goal
                onFailure(exception)
            }
    }


    fun readGoal(goalId: String, onSuccess: (Goal) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("goals").document(goalId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Goal::class.java)?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateGoal(
        goalId: String,
        updatedData: Map<String, Any>,
        onSuccess: () -> Unit,
        onFailure: (Exception) -> Unit
    ) {
        db.collection("goals").document(goalId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteGoal(goalId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("goals").document(goalId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Mark goal as achieved
    fun markGoalAsAchieved(goalId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val updatedData = mapOf("isAchieved" to true)
        updateGoal(goalId, updatedData, onSuccess, onFailure)
    }
}