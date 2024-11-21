package com.example.academictrackerapp.elvis.home

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.Imraan.Chatbot.MainChatBotctivity
import com.example.academictrackerapp.Imraan.TimeTable.TimetableActivity
import com.example.academictrackerapp.Imraan.grade.MarksActivity
import com.example.academictrackerapp.MainActivity
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentDashboardBinding
import com.google.firebase.firestore.FirebaseFirestore

class DashboardFragment : Fragment() {
    private var _binding: FragmentDashboardBinding? = null
    private val binding get() = _binding!!
    private lateinit var firestore: FirebaseFirestore
    private val userId = MainActivity.auth.currentUser?.uid //user id collected from main activity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentDashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        firestore = FirebaseFirestore.getInstance()

        // Loading user information by calling the funLoadUser Function
        funLoadUser()

        //Navigation
        setupNavigation()

        return root
    }

    //retrieving user data and verifying if a user is logged in
    private fun funLoadUser() {
        userId?.let { id ->
            firestore.collection("users").document(id)
                .get()
                .addOnSuccessListener { document ->
                    if (document != null && document.exists()) {
                        val username = document.getString("fullName") //document name essentially
                        binding.userNameText.text = "Hello $username!"
                    } else {
                        binding.userNameText.text = "Hello!"
                    }
                }
                .addOnFailureListener { exception ->
                    binding.userNameText.text = "Hello!"
                }
        } ?: run {
            binding.userNameText.text = "Hello!"
        }
    }

    private fun setupNavigation() {
        //binding.flashcardsCard.setOnClickListener { navigateTo(R.id.toFlashcards) }
        binding.goalSettingCard.setOnClickListener { navigateTo(R.id.toGoalsSettings) }
        binding.chatbotCard.setOnClickListener {
            val intent = Intent(requireContext(), MainChatBotctivity::class.java)
            startActivity(intent)
        }
        binding.timetableCard.setOnClickListener {
            val intent = Intent(requireContext(), TimetableActivity::class.java)
            startActivity(intent)
        }
        binding.gradeCard.setOnClickListener {
            val intent = Intent(requireContext(), MarksActivity::class.java)
            startActivity(intent)
        }
        binding.dashboardCard.setOnClickListener { navigateTo(R.id.toDashboard) }
        binding.remindersCard.setOnClickListener { navigateTo(R.id.toReminders) }
        binding.supportCard.setOnClickListener { navigateTo(R.id.toSupport) }

    }

    private fun navigateTo(destinationId: Int) {
        findNavController().navigate(destinationId)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
