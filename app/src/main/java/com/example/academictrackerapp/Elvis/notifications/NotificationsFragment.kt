package com.example.academictrackerapp.elvis.notifications

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.work.Data
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.WorkRequest
import com.example.academictrackerapp.databinding.FragmentNotificationsBinding
import com.example.academictrackerapp.databinding.ReminderDialogBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.example.academictrackerapp.MainActivity
import com.example.academictrackerapp.R
import com.example.academictrackerapp.elvis.data.Reminder
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var firestore: FirebaseFirestore
    private val userId = MainActivity.auth.currentUser?.uid
    private val remindersList = mutableListOf<Reminder>()
    private lateinit var adapter: ReminderAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance()

        adapter = ReminderAdapter(remindersList, firestore)
        binding.remindersRecyclerView.adapter = adapter
        binding.remindersRecyclerView.layoutManager = LinearLayoutManager(requireContext())

        binding.addReminder.setOnClickListener {
            if (userId != null) {
                addReminderDialog() // Opens the dialog for user input
            } else {
                Toast.makeText(requireContext(), "You must be logged in to add reminders", Toast.LENGTH_SHORT).show()
            }
        }

        showCreatedNotifications()
        return binding.root
    }

    private fun addReminderDialog() {
        val dialogBinding = ReminderDialogBinding.inflate(layoutInflater)
        val dialog = AlertDialog.Builder(requireContext())
            .setView(dialogBinding.root)
            .show()
        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent)

        dialogBinding.remindertype.adapter = ArrayAdapter(
            requireContext(),
            android.R.layout.simple_list_item_1,
            requireContext().resources.getStringArray(R.array.ReminderTypes)
        )

        val pickedDate = Calendar.getInstance()
        dialogBinding.selectButton.setOnClickListener {
            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    pickedDate.set(year, month, dayOfMonth, hourOfDay, minute)
                    dialogBinding.pickedDateAndTime.text = SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(pickedDate.time)
                }, pickedDate.get(Calendar.HOUR_OF_DAY), pickedDate.get(Calendar.MINUTE), false).show()
            }, pickedDate.get(Calendar.YEAR), pickedDate.get(Calendar.MONTH), pickedDate.get(Calendar.DAY_OF_MONTH)).show()
        }

        dialogBinding.submitButton.setOnClickListener {
            val title = dialogBinding.etTitle.text.toString()
            val reminderType = dialogBinding.remindertype.selectedItem.toString()
            val timestamp = pickedDate.timeInMillis

            if (title.isEmpty()) {
                dialogBinding.etTitle.error = "Please provide a title"
            } else {
                saveReminderToFirestore(title, reminderType, timestamp)
                scheduleReminderNotification(title, timestamp)
                dialog.dismiss()
            }
        }

        dialogBinding.canceButton.setOnClickListener {
            dialog.dismiss()
        }
    }

    private fun saveReminderToFirestore(title: String, reminderType: String, timestamp: Long) {
        val reminder = hashMapOf(
            "title" to title,
            "reminderType" to reminderType,
            "timestamp" to timestamp,
            "userId" to userId
        )

        firestore.collection("reminders")
            .add(reminder)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Reminder added", Toast.LENGTH_SHORT).show()
                showCreatedNotifications()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Failed to add reminder: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showCreatedNotifications() {
        firestore.collection("reminders")
            .whereEqualTo("userId", userId)
            .get()
            .addOnSuccessListener { documents ->
                remindersList.clear()
                for (document in documents) {
                    val reminder = Reminder(
                        id = document.id,
                        title = document.getString("title") ?: "",
                        reminderType = document.getString("reminderType") ?: "",
                        timestamp = document.getLong("timestamp") ?: 0L
                    )
                    remindersList.add(reminder)
                }
                adapter.notifyDataSetChanged()
            }
            .addOnFailureListener { e ->
                Toast.makeText(context, "Failed to load reminders: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun scheduleReminderNotification(title: String, timestamp: Long) {
        val delay = timestamp - System.currentTimeMillis()
        if (delay > 0) {
            val data = Data.Builder()
                .putString("Title", title)
                .putString("Message", "It's time for your reminder!")
                .build()

            val notificationWorkRequest: WorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
                .setInitialDelay(delay, TimeUnit.MILLISECONDS)
                .setInputData(data)
                .build()

            WorkManager.getInstance(requireContext()).enqueue(notificationWorkRequest)
        } else {
            Toast.makeText(requireContext(), "Cannot schedule a reminder for the past!", Toast.LENGTH_SHORT).show()
        }
    }
}
