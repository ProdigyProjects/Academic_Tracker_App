package com.example.academictrackerapp.elvis.notifications

import android.app.AlertDialog
import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.academictrackerapp.databinding.ReminderItemBinding
import com.example.academictrackerapp.elvis.data.Reminder
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ReminderAdapter(
    private val reminders: MutableList<Reminder>, // Change to MutableList
    private val firestore: FirebaseFirestore
) : RecyclerView.Adapter<ReminderAdapter.ReminderViewHolder>() {

    inner class ReminderViewHolder(private val binding: ReminderItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(reminder: Reminder) {
            binding.reminderNameEditText.setText(reminder.title)
            binding.reminderDateEditText.setText(
                SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date(reminder.timestamp))
            )

            // Delete button functionality with alert dialog confirmation
            binding.imageButton4.setOnClickListener {
                AlertDialog.Builder(binding.root.context)
                    .setTitle("Delete Reminder")
                    .setMessage("Are you sure you want to delete this reminder?")
                    .setPositiveButton("Delete") { dialog, _ ->
                        firestore.collection("reminders").document(reminder.id)
                            .delete()
                            .addOnSuccessListener {
                                Toast.makeText(binding.root.context, "Reminder deleted", Toast.LENGTH_SHORT).show()
                                // Remove the item from the mutable list and notify the adapter
                                val position = adapterPosition
                                reminders.removeAt(position)
                                notifyItemRemoved(position)
                            }
                            .addOnFailureListener { e ->
                                Toast.makeText(binding.root.context, "Failed to delete reminder: ${e.message}", Toast.LENGTH_LONG).show()
                            }
                        dialog.dismiss()
                    }
                    .setNegativeButton("Cancel") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .show()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReminderViewHolder {
        val binding = ReminderItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReminderViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ReminderViewHolder, position: Int) {
        holder.bind(reminders[position])
    }

    override fun getItemCount(): Int = reminders.size
}
