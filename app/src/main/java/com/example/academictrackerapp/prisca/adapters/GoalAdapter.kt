package com.example.academictrackerapp.prisca.adapters

import android.annotation.SuppressLint
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.academictrackerapp.databinding.FragmentSingleItemBinding
import com.example.academictrackerapp.prisca.model.database.Goal
import com.google.firebase.firestore.FieldPath
import com.google.firebase.firestore.FirebaseFirestore

class GoalAdapter(
    private val goals: List<Goal>,
    private val onGoalClick: (Goal) -> Unit,
    private val onEditClick: (Goal) -> Unit
) : RecyclerView.Adapter<GoalAdapter.GoalViewHolder>() {

    inner class GoalViewHolder(private val binding: FragmentSingleItemBinding) : RecyclerView.ViewHolder(binding.root) {
        init {
            binding.root.setOnClickListener {
                val goal = goals[adapterPosition]
                onGoalClick(goal)
            }
            /*binding.editGoalButton.setOnClickListener {
                val goal = goals[adapterPosition]
                onEditClick(goal)
            }*/
        }

        @SuppressLint("SetTextI18n")
        fun bind(goal: Goal) {
            // Set the goal title, date, and time
            binding.titleView.text = goal.title
            binding.dueDate.text = goal.dueDate
            binding.timeView.text = goal.reminderId

            countAndDisplayHabits(goal.habitIds)
            countAndDisplayTasks(goal.taskIds)

            // Optional: Set any image for the goal if you have one in your Goal object
            // binding.goalImage.setImageResource(R.drawable.sample_image)
        }

        private fun countAndDisplayHabits(habitIds: List<Any>) {
            if (habitIds.isEmpty()) {
                binding.habitsText.text = "Habits 0/4"
                return
            }

            // Filter out empty strings or null values if any
            val validHabitIds = habitIds.filter { it is String && it.isNotEmpty() } as List<String>

            if (validHabitIds.isEmpty()) {
                binding.habitsText.text = "Habits 0/4"
                return
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("habits")
                .whereIn(FieldPath.documentId(), validHabitIds)
                .get()
                .addOnSuccessListener { documents ->
                    binding.habitsText.text = "Habits ${documents.size()}/4"
                }
                .addOnFailureListener { exception ->
                    Log.w("GoalAdapter", "Error getting habits: ", exception)
                    binding.habitsText.text = "Habits: Error"
                }
        }

        private fun countAndDisplayTasks(taskIds: List<String>) {
            if (taskIds.isEmpty()) {
                binding.tasksText.text = "Tasks 0/3"
                return
            }

            // Filter out empty strings if any
            val validTaskIds = taskIds.filter { it.isNotEmpty() }

            if (validTaskIds.isEmpty()) {
                binding.tasksText.text = "Tasks 0/3"
                return
            }

            val db = FirebaseFirestore.getInstance()
            db.collection("tasks")
                .whereIn(FieldPath.documentId(), validTaskIds)
                .get()
                .addOnSuccessListener { documents ->
                    binding.tasksText.text = "Tasks ${documents.size()}/3"
                }
                .addOnFailureListener { exception ->
                    Log.w("GoalAdapter", "Error getting tasks: ", exception)
                    binding.tasksText.text = "Tasks: Error"
                }
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): GoalViewHolder {
        val binding = FragmentSingleItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GoalViewHolder(binding)
    }

    override fun onBindViewHolder(holder: GoalViewHolder, position: Int) {
        val goal = goals[position]
        holder.bind(goal)
    }

    override fun getItemCount(): Int = goals.size
}
