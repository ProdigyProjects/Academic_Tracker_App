package com.example.academictrackerapp.prisca.controller

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class PraiseFragment : Fragment() {

    private lateinit var confirmButton: Button
    private lateinit var mDatabase: DatabaseReference
    private var currentUser: FirebaseUser? = null

    private val goalId = "" // Replace with actual goal ID if needed

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_praise, container, false)

        // Initialize Firebase references
        mDatabase = FirebaseDatabase.getInstance().reference
        currentUser = FirebaseAuth.getInstance().currentUser

        // Initialize the confirm button
        confirmButton = view.findViewById(R.id.confirm_button)

        // Set click listener for the confirm button
        confirmButton.setOnClickListener {
            // Save goal as achieved in Firebase
            currentUser?.let {
                //saveGoalStatus()
                requireActivity().onBackPressed()
            }
        }

        return view
    }

    private fun saveGoalStatus() {
        // Save the goal status in Firebase
        currentUser?.let { user ->
            mDatabase.child("users")
                .child(user.uid)
                .child("goals")
                .child(goalId)
                .child("status")
                .setValue("isAchieved")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Navigate to goal list fragment
                        findNavController().navigate(R.id.toGoalList) // Ensure this ID is correct
                    }
                }
        }
    }
}
