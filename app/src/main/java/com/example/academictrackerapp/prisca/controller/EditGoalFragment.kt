package com.example.academictrackerapp.prisca.controller

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentEditBinding // Import the generated binding class
import com.example.academictrackerapp.prisca.model.database.Goal
import com.example.academictrackerapp.prisca.model.repositories.GoalRepository
import java.util.*

class EditGoalFragment : Fragment() {

    private lateinit var binding: FragmentEditBinding // Declare the binding variable
    private lateinit var goalRepository: GoalRepository

    private var dueDate: String = ""
    private var reminderTime: String = ""

    private var goalId: Int? = null

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditBinding.inflate(inflater, container, false) // Inflate the binding

        // Initialize the GoalRepository
        goalRepository = GoalRepository()

        // Retrieve the goalId argument
        goalId = arguments?.getInt("goalId")

        // Load the goal data
        loadGoalData()

        // Set up listeners
        binding.dueDateText.setOnClickListener { showDatePicker() }
        binding.cancelButton.setOnClickListener { findNavController().navigate(R.id.toGoalList) }
        binding.submitButton.setOnClickListener { updateGoal() }

        return binding.root // Return the root view from the binding
    }

    private fun loadGoalData() {
        if (goalId == null) {
            Log.e("EditGoalFragment", "Goal ID is null or empty")
            return
        }
        goalRepository.readGoal(goalId.toString(), { goal ->
            binding.goalTitleInput.setText(goal.title)
            binding.goalDescriptionInput.setText(goal.description)
            binding.goalCategory.setText(goal.category)
            binding.dueDateText.text = goal.dueDate
            binding.reminderTimeText.text = goal.reminderId // Assuming you store reminder ID or time

            // If you want to do something with noteId, habitIds, taskIds
            //val noteId = goal.noteId
            val habitIds = goal.habitIds
            val taskIds = goal.taskIds


        }, { exception ->
            Log.e("EditGoalFragment", "Error loading goals")
        })
    }

    private fun updateGoal() {
        val updatedGoal = Goal(
            id = goalId.toString(),
            title = binding.goalTitleInput.text.toString(),
            description = binding.goalDescriptionInput.text.toString(),
            category = binding.goalCategory.text.toString(),
            dueDate = dueDate,
            reminderId = reminderTime,
            //note = binding.noteInput.text.toString(),
            habitIds = listOf("habit1", "habit2"),
            taskIds = listOf("task1", "task2")
        )

        goalRepository.updateGoal(goalId.toString(), updatedGoal.toMap(), {
            // Goal updated successfully, navigate back
            findNavController().navigate(R.id.toGoalList)
        }, { exception ->
            Log.e("EditGoalFragment", "Error updating goals")
        })
    }

    private fun showDatePicker() {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog = DatePickerDialog(requireContext(), { _, selectedYear, selectedMonth, selectedDay ->
            dueDate = "$selectedDay/${selectedMonth + 1}/$selectedYear"
            binding.dueDateText.text = dueDate
        }, year, month, day)

        datePickerDialog.show()
    }
}