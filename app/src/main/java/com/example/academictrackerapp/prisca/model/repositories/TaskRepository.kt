package com.example.academictrackerapp.prisca.model.repositories

import com.example.academictrackerapp.prisca.model.database.Task
import com.google.firebase.firestore.FirebaseFirestore

class TaskRepository {

    val db = FirebaseFirestore.getInstance()

    fun isTaskCollectionCreated(onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks").limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createTask(task: Task, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        val db = FirebaseFirestore.getInstance()

        // Add the task to Firestore and get the generated document ID
        val taskRef = db.collection("tasks").add(task)
            .addOnSuccessListener { documentReference ->
                // Assign the generated ID to the task object
                task.id = documentReference.id

                // Now that the ID is set, resave the task (update the task)
                db.collection("tasks").document(task.id!!).set(task)
                    .addOnSuccessListener {
                        onSuccess()  // Notify success after resaving the task with the ID
                    }
                    .addOnFailureListener { exception ->
                        onFailure(exception)  // Handle failure if resaving fails
                    }
            }
            .addOnFailureListener { exception ->
                onFailure(exception)  // Handle failure during initial creation
            }
    }



    fun readTask(taskId: String, onSuccess: (Task) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks").document(taskId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Task::class.java)?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateTask(taskId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks").document(taskId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteTask(taskId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks").document(taskId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    // Fetch tasks associated with a specific goal
    fun getTasksByGoalId(goalId: String, onSuccess: (List<Task>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("tasks").whereEqualTo("goalId", goalId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Task::class.java)
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

}