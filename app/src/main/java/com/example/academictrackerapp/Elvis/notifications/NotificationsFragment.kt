package com.example.academictrackerapp.elvis.notifications

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.NotificationManagerCompat
import androidx.fragment.app.Fragment
import androidx.work.OneTimeWorkRequestBuilder
import androidx.work.WorkManager
import androidx.work.workDataOf
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentNotificationsBinding
import com.example.academictrackerapp.databinding.ReminderDialogBinding
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.firestore.FirebaseFirestore
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.concurrent.TimeUnit

class NotificationsFragment : Fragment() {

    private lateinit var binding: FragmentNotificationsBinding
    private lateinit var firestore: FirebaseFirestore

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentNotificationsBinding.inflate(inflater, container, false)
        firestore = FirebaseFirestore.getInstance() // Initialize Firestore

        binding.addReminder.setOnClickListener {
            addReminder()
        }

        return binding.root
    }

    private fun addReminder() {
        if (Build.VERSION.SDK_INT >= 33 && !NotificationManagerCompat.from(requireContext()).areNotificationsEnabled()) {
            showNotificationPermissionDialog()
        } else {
            addReminderDialog()
        }
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

        dialogBinding.canceButton.setOnClickListener {
            dialog.dismiss()
        }

        val pickedDate = Calendar.getInstance()
        dialogBinding.selectButton.setOnClickListener {
            val year = pickedDate.get(Calendar.YEAR)
            val month = pickedDate.get(Calendar.MONTH)
            val day = pickedDate.get(Calendar.DAY_OF_MONTH)
            val hour = pickedDate.get(Calendar.HOUR_OF_DAY)
            val minute = pickedDate.get(Calendar.MINUTE)

            DatePickerDialog(requireContext(), { _, year, month, dayOfMonth ->
                TimePickerDialog(requireContext(), { _, hourOfDay, minute ->
                    pickedDate.set(year, month, dayOfMonth, hourOfDay, minute)
                    Log.d("Date And Time", "Picked Date and Time $pickedDate")
                    dialogBinding.pickedDateAndTime.text = getCurrentDateAndTime(pickedDate.timeInMillis)
                }, hour, minute, false).show()
            }, year, month, day).show()
        }

        dialogBinding.submitButton.setOnClickListener {
            if (dialogBinding.etTitle.text.isNullOrEmpty()) {
                dialogBinding.etTitle.requestFocus()
                dialogBinding.etTitle.error = "Please Provide Title"
            } else if (dialogBinding.pickedDateAndTime.text == requireContext().resources.getString(R.string.date_and_time)) {
                dialogBinding.pickedDateAndTime.error = "Please select Date and Time"
            } else {
                val timeDelayInSeconds = (pickedDate.timeInMillis / 1000L) - (Calendar.getInstance().timeInMillis / 1000L)
                if (timeDelayInSeconds < 0) {
                    Toast.makeText(requireContext(), "Can't set reminders for past", Toast.LENGTH_LONG).show()
                    return@setOnClickListener
                }

                // Save reminder to Firestore
                saveReminderToFirestore(
                    dialogBinding.etTitle.text.toString(),
                    requireContext().resources.getStringArray(R.array.ReminderTypes)[dialogBinding.remindertype.selectedItemPosition],
                    pickedDate.timeInMillis
                )

                // WorkManager setup
                createWorkRequest(dialogBinding.etTitle.text.toString(),
                    requireContext().resources.getStringArray(R.array.ReminderTypes)[dialogBinding.remindertype.selectedItemPosition],
                    timeDelayInSeconds)

                Toast.makeText(requireContext(), "Reminder Added", Toast.LENGTH_LONG).show()
                dialog.dismiss()
            }
        }
    }

    private fun createWorkRequest(title: String, reminderType: String, delay: Long) {
        val reminderWorkRequest = OneTimeWorkRequestBuilder<ReminderWorker>()
            .setInitialDelay(delay, TimeUnit.SECONDS)
            .setInputData(workDataOf("Title" to "Todo: $reminderType", "Message" to title))
            .build()
        WorkManager.getInstance(requireContext()).enqueue(reminderWorkRequest)
    }

    private fun getCurrentDateAndTime(millis: Long): String {
        return SimpleDateFormat("dd-MM-yyyy hh:mm a", Locale.getDefault()).format(Date(millis))
    }

    private fun saveReminderToFirestore(title: String, reminderType: String, timestamp: Long) {
        val reminder = hashMapOf(
            "title" to title,
            "reminderType" to reminderType,
            "timestamp" to timestamp,
            "createdAt" to System.currentTimeMillis()
        )

        firestore.collection("reminders") //table name
            .add(reminder)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Reminder saved to Firestore", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener { e ->
                Toast.makeText(requireContext(), "Error saving reminder: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }

    private fun showNotificationPermissionDialog() {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission required to show notifications")
            .setPositiveButton("OK") { _, _ ->
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    notificationPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
                }
            }
            .setNegativeButton("Cancel", null)
            .show()
    }

    private val notificationPermissionLauncher = registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
        if (!isGranted) {
            if (Build.VERSION.SDK_INT >= 33) {
                if (shouldShowRequestPermissionRationale(android.Manifest.permission.POST_NOTIFICATIONS)) {
                    showNotificationPermissionDialog()
                } else {
                    showSettingsDialog()
                }
            }
        } else {
            Toast.makeText(requireContext(), "Notification permission granted", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showSettingsDialog() {
        MaterialAlertDialogBuilder(requireContext(), com.google.android.material.R.style.MaterialAlertDialog_Material3)
            .setTitle("Notification Permission")
            .setMessage("Notification permission required to show notifications")
            .setPositiveButton("OK") { _, _ ->
                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:${requireContext().applicationContext.packageName}")
                startActivity(intent)
            }
            .setNegativeButton("Cancel", null)
            .show()
    }
}
