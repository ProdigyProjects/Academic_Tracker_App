package com.example.academictrackerapp.prisca.controller

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentNoGoalBinding

class NoGoalFragment : Fragment() {

    // Declare the binding variable
    private var _binding: FragmentNoGoalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout using view binding
        _binding = FragmentNoGoalBinding.inflate(inflater, container, false)
        val rootView = binding.root


        // Calendar View logic: Set current date
        binding.calendarView.date = System.currentTimeMillis()

        // Add goal button logic: navigate to CreateGoalFragment.kt
        binding.addGoalButton.setOnClickListener {
            findNavController().navigate(R.id.toCreateGoal)
        }

        binding.viewAllGoals.setOnClickListener {
            findNavController().navigate(R.id.toGoalList)
        }

        return rootView
    }

    override fun onDestroyView() {
        super.onDestroyView()
        // Avoid memory leaks by setting the binding to null when the view is destroyed
        _binding = null
    }
}
