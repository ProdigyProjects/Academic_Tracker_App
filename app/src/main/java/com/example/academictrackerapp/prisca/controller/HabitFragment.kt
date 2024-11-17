package com.example.academictrackerapp.prisca.controller

import android.app.DatePickerDialog
import android.app.TimePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.Toast
import androidx.core.view.children
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.prisca.model.database.Habit
import com.example.academictrackerapp.prisca.model.repositories.HabitRepository
import com.example.academictrackerapp.databinding.FragmentHabitBinding
import com.google.firebase.auth.FirebaseAuth
import java.text.SimpleDateFormat
import java.util.*

class HabitFragment : Fragment() {

    private var _binding: FragmentHabitBinding? = null
    private val binding get() = _binding!!

    private val habitRepository = HabitRepository()
    private var selectedDays: String = ""
    private var dueDate: String = ""
    private var reminderTime: String = ""
    private var habitId: String? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHabitBinding.inflate(inflater, container, false)
        val view = binding.root

        habitId = arguments?.getString("habitId")

        // Set up click listeners
        binding.calendarIcon.setOnClickListener {
            showDatePicker()
        }

        binding.clockIcon.setOnClickListener {
            showTimePicker()
        }

        setupSaveHabitButton()
        setupDeleteButton()

        return view
    }

    private fun setupDeleteButton() {
        binding.deleteButton.setOnClickListener {
            deleteHabit()
        }
    }

    private fun deleteHabit() {
        habitId?.let {
            habitRepository.deleteHabit(it, {
                Toast.makeText(requireContext(), "Habit deleted successfully", Toast.LENGTH_SHORT).show()
                findNavController().navigateUp()
            }, { exception ->
                Toast.makeText(requireContext(), "Error deleting habit: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        } ?: run {
            Toast.makeText(requireContext(), "No habit to delete", Toast.LENGTH_SHORT).show()
        }
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            { _, year, month, dayOfMonth ->
                val selectedDate = Calendar.getInstance().apply {
                    set(year, month, dayOfMonth)
                }
                // Store the due date as a formatted string
                dueDate = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(selectedDate.time)
                // Update the TextView to display the selected date
                binding.dueDateText.text = dueDate
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        datePickerDialog.show()
    }

    private fun showTimePicker() {
        val calendar = Calendar.getInstance()
        val timePickerDialog = TimePickerDialog(
            requireContext(),
            { _, hourOfDay, minute ->
                val selectedTime = Calendar.getInstance().apply {
                    set(Calendar.HOUR_OF_DAY, hourOfDay)
                    set(Calendar.MINUTE, minute)
                }
                // Store the reminder time as a formatted string
                reminderTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(selectedTime.time)
                // Update the TextView to display the selected time
                binding.reminderTimeText.text = reminderTime
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        timePickerDialog.show()
    }


    private fun setupSaveHabitButton() {
        binding.saveHabitButton.setOnClickListener {
            val habitTitle = binding.habitTitleInput.text.toString()
            val note = binding.noteInput.text.toString()
            selectedDays = getSelectedDays()

            if (habitTitle.isEmpty() || selectedDays.isEmpty() || dueDate.isEmpty()) {
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Check if the user is authenticated
            val currentUser = FirebaseAuth.getInstance().currentUser
            if (currentUser == null) {
                Toast.makeText(requireContext(), "User not authenticated. Please log in.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create the habit object without the ID
            val habit = Habit(
                title = habitTitle,
                daysOfWeek = selectedDays,
                note = note,
                dueDate = dueDate,
                reminderTime = reminderTime
            )

            // Save the habit and handle success/failure
            habitRepository.createHabit(habit, {
                Toast.makeText(requireContext(), "Habit saved successfully", Toast.LENGTH_SHORT).show()
                clearInputs()
                findNavController().navigate(R.id.toCreateGoal)
            }, { exception ->
                Log.e("HabitFragment", "Error saving habit", exception)
                Toast.makeText(requireContext(), "Error saving habit: ${exception.message}", Toast.LENGTH_SHORT).show()
            })
        }
    }

    private fun getSelectedDays(): String {
        val days = mutableListOf<String>()
        val checkBoxes = listOf(
            binding.checkboxSun,
            binding.checkboxMon,
            binding.checkboxTue,
            binding.checkboxWed,
            binding.checkboxThu,
            binding.checkboxFri,
            binding.checkboxSat
        )

        checkBoxes.forEachIndexed { index, checkBox ->
            if (checkBox.isChecked) {
                days.add(when (index) {
                    0 -> "Sunday"
                    1 -> "Monday"
                    2 -> "Tuesday"
                    3 -> "Wednesday"
                    4 -> "Thursday"
                    5 -> "Friday"
                    6 -> "Saturday"
                    else -> ""
                })
            }
        }
        return days.joinToString(", ")
    }

    private fun clearInputs() {
        binding.habitTitleInput.text.clear()
        binding.noteInput.text.clear()
        selectedDays = ""
        dueDate = ""
        reminderTime = ""
        binding.daySelector.children.forEach { (it as? CheckBox)?.isChecked = false }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}