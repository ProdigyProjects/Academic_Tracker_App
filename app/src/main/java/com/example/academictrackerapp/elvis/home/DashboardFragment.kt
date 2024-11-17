package com.example.academictrackerapp.elvis.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.academictrackerapp.MainActivity
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


    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
