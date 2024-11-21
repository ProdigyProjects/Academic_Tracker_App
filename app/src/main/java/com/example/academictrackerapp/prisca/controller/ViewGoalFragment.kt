package com.example.academictrackerapp.prisca.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentViewGoalBinding
import com.google.firebase.firestore.FirebaseFirestore

class ViewGoalFragment : Fragment() {

    private var _binding: FragmentViewGoalBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentViewGoalBinding.inflate(inflater, container, false)

        val goalId = arguments?.getString("goalId") ?: return binding.root.also {
            Toast.makeText(requireContext(), "Goal ID is missing", Toast.LENGTH_SHORT).show()
        }

        fetchGoalData(goalId)

        binding.buttonAchieved.setOnClickListener {
            markGoalAsAchieved(goalId)
        }

        binding.buttonDelete.setOnClickListener {
            deleteGoal(goalId)
        }

        return binding.root
    }

    private fun fetchGoalData(goalId: String) {
        db.collection("goals").document(goalId)
            .get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.goalTitle.text = document.getString("title") ?: "No Title"
                    binding.goalDescription.text = document.getString("description") ?: "No Description"
                    binding.goalCategory.text = document.getString("category") ?: "No Category"
                    binding.dueDate.text = document.getString("dueDate") ?: "No Due Date"
                    binding.reminderTime.text = document.getString("reminderId") ?: "No Reminder Time"

                    val isAchieved = document.getBoolean("isAchieved") ?: false
                    binding.status.text = if (isAchieved) {
                        "Completed"
                    } else {
                        "Not Completed"
                    }
                } else {
                    Toast.makeText(requireContext(), "Goal not found", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Log.e("ViewGoalFragment", "Error fetching goal: ${exception.message}")
                Toast.makeText(requireContext(), "Error fetching goal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun markGoalAsAchieved(goalId: String) {
        db.collection("goals").document(goalId)
            .update("isAchieved", true)
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Goal marked as achieved", Toast.LENGTH_SHORT).show()
                findNavController().navigate(R.id.toPraise)
            }
            .addOnFailureListener { exception ->
                Log.e("ViewGoalFragment", "Error marking goal as achieved: ${exception.message}")
                Toast.makeText(requireContext(), "Error marking goal as achieved: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    private fun deleteGoal(goalId: String) {
        db.collection("goals").document(goalId)
            .delete()
            .addOnSuccessListener {
                Toast.makeText(requireContext(), "Goal deleted successfully", Toast.LENGTH_SHORT).show()
                requireActivity().onBackPressed()
            }
            .addOnFailureListener { exception ->
                Log.e("ViewGoalFragment", "Error deleting goal: ${exception.message}")
                Toast.makeText(requireContext(), "Error deleting goal: ${exception.message}", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}