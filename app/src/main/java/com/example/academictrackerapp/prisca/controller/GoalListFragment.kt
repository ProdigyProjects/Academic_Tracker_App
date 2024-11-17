package com.example.academictrackerapp.prisca.controller

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentGoalListBinding
import com.example.academictrackerapp.prisca.adapters.GoalAdapter
import com.example.academictrackerapp.prisca.model.database.Goal
import com.example.academictrackerapp.prisca.model.repositories.GoalRepository

class GoalListFragment : Fragment() {

    private var _binding: FragmentGoalListBinding? = null
    private val binding get() = _binding!!
    private lateinit var goalAdapter: GoalAdapter
    private val goalRepository = GoalRepository()
    private val goals = mutableListOf<Goal>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGoalListBinding.inflate(inflater, container, false)

        setupRecyclerView()
        loadGoals(true) // Load ongoing goals by default
        return binding.root
    }

    private fun setupRecyclerView() {
        binding.recyclerViewGoals.layoutManager = LinearLayoutManager(context)
        goalAdapter = GoalAdapter(goals, { goal ->
            // Navigate to ViewGoalFragment using action ID
            val actionId = R.id.toViewGoal
            val bundle = Bundle().apply {
                putString("goalId", goal.id) // Pass goal.id as a String
            }
            findNavController().navigate(actionId, bundle)
        }, { goal ->
            // Navigate to EditGoalFragment using action ID
            val actionId = R.id.toEdit
            val bundle = Bundle().apply {
                putString("goalId", goal.id) // Pass goal.id as a String
            }
            findNavController().navigate(actionId, bundle)
        })
        binding.recyclerViewGoals.adapter = goalAdapter
    }


    private fun loadGoals(isOngoing: Boolean) {
        goals.clear()
        goalRepository.db.collection("goals")
            //.whereEqualTo("isAchieved", !isOngoing)
            .get()
            .addOnSuccessListener { documents ->
                Log.d("GoalListFragment", "Number of goals: ${documents.size()}")
                if (documents.isEmpty) {
                    Log.d("GoalListFragment", "No goals found")
                    binding.errorMessage.visibility = View.VISIBLE
                    binding.recyclerViewGoals.visibility = View.GONE
                } else {
                    for (document in documents) {
                        val goal = document.toObject(Goal::class.java)
                        goal.id = document.id // Set the Firestore document ID
                        goals.add(goal)
                    }
                    goalAdapter.notifyDataSetChanged()
                    binding.errorMessage.visibility = View.GONE
                    binding.recyclerViewGoals.visibility = View.VISIBLE
                }
            }
            .addOnFailureListener { exception ->
                handleError(exception)
            }
    }


    private fun handleError(exception: Exception) {
        Log.e("GoalListFragment", "Error loading goals: ${exception.message}")
        binding.errorMessage.visibility = View.VISIBLE
        binding.recyclerViewGoals.visibility = View.GONE
        Toast.makeText(requireContext(), "Error loading goals: ${exception.message}", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
