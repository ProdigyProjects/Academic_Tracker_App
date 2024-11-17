package com.example.academictrackerapp.prisca.model.repositories

import com.example.academictrackerapp.prisca.model.database.Note
import com.google.firebase.firestore.FirebaseFirestore

class NoteRepository {

    val db = FirebaseFirestore.getInstance()

    fun isNoteCollectionCreated(onResult: (Boolean) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").limit(1)
            .get()
            .addOnSuccessListener { querySnapshot ->
                onResult(!querySnapshot.isEmpty)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }

    fun createNote(note: Note, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").add(note)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun readNote(noteId: String, onSuccess: (Note) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").document(noteId)
            .get()
            .addOnSuccessListener { document ->
                document.toObject(Note::class.java)?.let { onSuccess(it) }
            }
            .addOnFailureListener { onFailure(it) }
    }

    fun updateNote(noteId: String, updatedData: Map<String, Any>, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").document(noteId)
            .update(updatedData)
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }

    fun deleteNote(noteId: String, onSuccess: () -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").document(noteId)
            .delete()
            .addOnSuccessListener { onSuccess() }
            .addOnFailureListener { onFailure(it) }
    }
    // Fetch tasks associated with a specific goal
    fun getNotesByGoalId(goalId: String, onSuccess: (List<Note>) -> Unit, onFailure: (Exception) -> Unit) {
        db.collection("notes").whereEqualTo("goalId", goalId)
            .get()
            .addOnSuccessListener { querySnapshot ->
                val tasks = querySnapshot.toObjects(Note::class.java)
                onSuccess(tasks)
            }
            .addOnFailureListener { exception ->
                onFailure(exception)
            }
    }
}