package com.example.academictrackerapp.prisca.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentTaskBinding
import com.example.academictrackerapp.prisca.model.database.Task
import com.example.academictrackerapp.prisca.model.repositories.TaskRepository
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class TaskFragment : Fragment() {

    private var _binding: FragmentTaskBinding? = null
    private val binding get() = _binding!!

    private val taskRepository = TaskRepository()
    private var dueDate: String = ""
    private var reminderTime: String = ""
    private var taskId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentTaskBinding.inflate(inflater, container, false)

        taskId = arguments?.getString("taskId")

        binding.calendarIcon.setOnClickListener { showDatePicker() }
        binding.clockIcon.setOnClickListener { showTimePicker() }
        binding.saveTaskButton.setOnClickListener { saveTask() }
        binding.deleteButton.setOnClickListener { deleteTask() }

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
                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                binding.dueDateText.text = dueDate
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
                reminderTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)
                binding.reminderTimeText.text = reminderTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        ).show()
    }

    private fun saveTask() {
        val taskTitle = binding.taskTitleInput.text.toString()
        val note = binding.noteInput.text.toString()

        if (taskTitle.isEmpty() ||  dueDate.isEmpty() || reminderTime.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            return
        }

        // Check if the user is authenticated
        val currentUser = FirebaseAuth.getInstance().currentUser
        if (currentUser == null) {
            Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show()
            return
        }

        // Create the task object without the ID
        val task = Task(
            title = taskTitle,
            dueDate = dueDate,
            note = note,
            reminderTime = reminderTime
        )

        // Save the task and handle success/failure
        taskRepository.createTask(task, {
            Toast.makeText(requireContext(), "Task saved successfully", Toast.LENGTH_SHORT).show()
            clearInputs()  // Clear inputs after saving
            findNavController().navigate(R.id.toCreateGoal)  // Navigate to task list or appropriate screen
        }, { exception ->
            Log.e("TaskFragment", "Error saving task", exception)
            Toast.makeText(requireContext(), "Error saving task: ${exception.message}", Toast.LENGTH_SHORT).show()
        })

    }

    private fun deleteTask() {
        taskId?.let {
            taskRepository.deleteTask(it, {
                Toast.makeText(requireContext(), "Task deleted successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }, { exception ->
                Toast.makeText(requireContext(), "Error deleting task: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        } ?: run {
            Toast.makeText(requireContext(), "No task to delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun clearInputs() {
        binding.taskTitleInput.text.clear()
        binding.noteInput.text.clear()
        dueDate = ""
        reminderTime = ""
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}