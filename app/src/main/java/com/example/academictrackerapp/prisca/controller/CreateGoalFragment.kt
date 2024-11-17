package com.example.academictrackerapp.prisca.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.MainActivity.Companion.auth
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentCreateGoalBinding
import com.example.academictrackerapp.prisca.model.database.Goal
import com.example.academictrackerapp.prisca.model.repositories.GoalRepository
import com.google.firebase.auth.FirebaseAuth
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.*

class CreateGoalFragment : Fragment() {

    private var _binding: FragmentCreateGoalBinding? = null
    private val binding get() = _binding!!

    private val goalRepository = GoalRepository()
    private var selectedDueDate: Long = 0
    private var selectedReminderTime: Long = 0
    private var imageUri: Uri? = null

    private lateinit var imagePickerLauncher: ActivityResultLauncher<Intent>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCreateGoalBinding.inflate(inflater, container, false)

        auth = FirebaseAuth.getInstance()

        imagePickerLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == AppCompatActivity.RESULT_OK) {
                val data = result.data
                imageUri = data?.data
                binding.addedImage.setImageURI(imageUri)
                binding.addedImage.visibility = View.VISIBLE
            }
        }

        binding.dueDateLayout.setOnClickListener { showDatePicker() }
        binding.reminderLayout.setOnClickListener { showTimePicker() }
        binding.createGoalButton.setOnClickListener { createGoal() }
        binding.addImageButton.setOnClickListener { openImagePicker() }
        binding.addHabitButton.setOnClickListener {
            findNavController().navigate(R.id.toHabit)
        }
        binding.addTaskButton.setOnClickListener {
            findNavController().navigate(R.id.toTask)
        }

        return binding.root
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                selectedDueDate = selectedDate.timeInMillis
                binding.dueDateText.text = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        ).show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                selectedReminderTime = selectedTime.timeInMillis
                binding.reminderTimeText.text = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun createGoal() {
        val title = binding.goalTitleInput.text.toString().trim()
        val description = binding.goalDescriptionInput.text.toString().trim()
        val category = binding.goalCategory.text.toString().trim()
        val note = binding.noteInput.text.toString().trim()

        if (title.isEmpty() || description.isEmpty() || category.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val base64Image = imageUri?.let { convertImageToBase64(it) } ?: ""

        // Get the habitIds, noteIds, taskIds, and userId
        val habitIds = listOf("habit1", "habit2") // Example: Replace with actual selected habit IDs
        val taskIds = listOf("task1", "task2") // Example: Replace with actual selected task IDs
        val userId = auth.currentUser?.uid ?: ""

        val goal = Goal(
            title = title,
            description = description,
            category = category,
            dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(Date(selectedDueDate)),
            reminderId = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(selectedReminderTime)),
            image = base64Image,
            habitIds = habitIds, // Set the habit ID
            note = note, // Set the note IDs
            taskIds = taskIds, // Set the task IDs
            userId = userId // Set the user ID
        )

        goalRepository.createGoal(goal, {
            Toast.makeText(requireContext(), "Goal created successfully", Toast.LENGTH_SHORT).show()
            if (isAdded) {
                findNavController().navigate(R.id.toGoalList)
            }
        }, { exception ->
            Toast.makeText(requireContext(), "Error creating goal: ${exception.message}", Toast.LENGTH_SHORT).show()
        })
    }


    private fun convertImageToBase64(imageUri: Uri): String? {
        return try {
            val inputStream: InputStream? = requireContext().contentResolver.openInputStream(imageUri)
            val bytes = inputStream?.readBytes()
            if (bytes != null) Base64.encodeToString(bytes, Base64.DEFAULT) else null
        } catch (e: Exception) {
            Log.e("CreateGoalFragment", "Error converting image to Base64: ${e.message}")
            null
        }
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK).apply {
            type = "image/*"
        }
        imagePickerLauncher.launch(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}