package com.example.academictrackerapp.katiana.registration

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.MainActivity
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentRegistrationBinding
import com.google.firebase.firestore.FirebaseFirestore

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    private val db = FirebaseFirestore.getInstance() // Firestore instance

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registrationViewModel =
            ViewModelProvider(this).get(RegistrationViewModel::class.java)

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.accountAlreadyExist.setOnClickListener {
            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
        }

        binding.registerButton.setOnClickListener {
            val profileImage = binding.profileImage.imageAlpha.toString()
            val fullName = binding.fullNameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val institutionName = binding.institutionNameInput.text.toString()
            val qualification = binding.qualificationInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if (email.isNotEmpty() && password.isNotEmpty()) {
                // Step 1: Create user with email and password
                MainActivity.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            val userId = MainActivity.auth.currentUser?.uid
                            // Step 2: Save user data to Firestore
                            val user = hashMapOf(
                                "userId" to userId,
                                "fullName" to fullName,
                                "email" to email,
                                "institutionName" to institutionName,
                                "qualification" to qualification,
                                "profileImage" to profileImage
                            )

                            userId?.let {
                                db.collection("users").document(it)
                                    .set(user)
                                    .addOnSuccessListener {
                                        // Step 3: After saving, log the user in
                                        MainActivity.auth.signInWithEmailAndPassword(email, password)
                                            .addOnCompleteListener { authResult ->
                                                if (authResult.isSuccessful) {
                                                    Toast.makeText(requireContext(), "User registered and logged in successfully!", Toast.LENGTH_LONG).show()
                                                    findNavController().navigate(R.id.action_registrationFragment_to_DashboardFragment)
                                                }
                                            }.addOnFailureListener { e ->
                                                Toast.makeText(requireContext(), "Login failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                            }
                                    }
                                    .addOnFailureListener { e ->
                                        Toast.makeText(requireContext(), "Failed to save user data: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                            }
                        } else {
                            Toast.makeText(requireContext(), "Registration failed: ${task.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            }
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
