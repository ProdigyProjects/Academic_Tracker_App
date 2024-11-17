package com.example.academictrackerapp.prisca.model.repositories

import com.example.academictrackerapp.prisca.model.database.Habit
import com.google.firebase.firestore.DocumentReference
import com.google.firebase.firestore.FirebaseFirestore


class HabitRepository {
    val db = FirebaseFirestore.getInstance()

    fun isHabitCollectionCreated(onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habits").limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createHabit(habit: Habit, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Add the habit to Firestore and get the generated document ID
        val habitRef = db.collection("habits").add(habit)
            .addOnSuccessListener { documentReference ->
                // Assign the generated ID to the habit object
                habit.id = documentReference.id

                // Now that the ID is set, resave the habit (update the habit)
                db.collection("habits").document(habit.id!!).set(habit)
                    .addOnSuccessListener {
                        onSuccess()  // Notify success after resaving the habit with the ID
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)  // Handle failure if resaving fails
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)  // Handle failure during initial creation
            }
    }


    fun readHabit(habitId: String, onSuccess: (Habit) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habits").document(habitId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Habit::class.java)?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateHabit(habitId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habits").document(habitId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteHabit(habitId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habits").document(habitId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    // Fetch tasks associated with a specific goal
    fun getHabitsByGoalId(goalId: String, onSuccess: (List<Habit>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("habits").whereEqualTo("goalId", goalId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Habit::class.java)
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}