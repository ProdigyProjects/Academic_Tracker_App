package com.example.academictrackerapp.prisca.controller

import android.annotation.SuppressLint
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.AlphaAnimation
import android.view.animation.Animation
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
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

    private val goalId = ""

    private lateinit var congratulationsText: TextView
    private lateinit var confettiImage: ImageView

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        val view: View = inflater.inflate(R.layout.fragment_praise, container, false)

        // Initialize Firebase references
        mDatabase = FirebaseDatabase.getInstance().reference
        currentUser = FirebaseAuth.getInstance().currentUser   // Correctly using FirebaseUser

        // Initialize the confirm button
        confirmButton = view.findViewById(R.id.confirm_button)

        // Set click listener for the confirm button
        confirmButton.setOnClickListener {
            // Save goal as achieved in Firebase
            currentUser?.let {
                saveGoalStatus()
            }
        }

        // Initialize congratulations views
        congratulationsText = view.findViewById(R.id.congratulationsText)
        confettiImage = view.findViewById(R.id.confettiImage)

        // Start the animation
        showCongratulations()

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
                .setValue("achieved")
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        // Navigate to goal list fragment
                        findNavController().navigate(R.id.toGoalList) // Ensure this ID is correct
                    }
                }
        }
    }

    private fun showCongratulations() {
        // Fade in the congratulations text
        congratulationsText.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000 // 1 second
            setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    // Show confetti after text fades in
                    showConfetti()
                }

                override fun onAnimationRepeat(animation: Animation?) {}
            })
        }
        congratulationsText.startAnimation(fadeIn)
    }

    private fun showConfetti() {
        // Show confetti image and animate it
        confettiImage.visibility = View.VISIBLE
        val fadeIn = AlphaAnimation(0f, 1f).apply {
            duration = 1000 // 1 second
        }
        confettiImage.startAnimation(fadeIn)
    }
}
