package com.example.academictrackerapp.katiana.userProfile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.academictrackerapp.MainActivity
import com.example.academictrackerapp.databinding.FragmentProfileBinding
import com.google.firebase.auth.EmailAuthProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore


class UserProfileFragment : Fragment() {

    private var _binding: FragmentProfileBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()
    private val userId = MainActivity.auth.currentUser?.uid
    private val firebaseAuth = FirebaseAuth.getInstance() // Initialize FirebaseAuth

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProfileBinding.inflate(inflater, container, false)
        val root: View = binding.root

        userId?.let { loadUserData(it) }

        binding.updateButton.setOnClickListener {
            updateUserProfile()
        }

        return root
    }

    private fun loadUserData(userId: String) {
        db.collection("users").document(userId).get()
            .addOnSuccessListener { document ->
                if (document != null) {
                    binding.fullNameInput.setText(document.getString("fullName"))
                    binding.emailInput.setText(document.getString("email"))
                    binding.institutionNameInput.setText(document.getString("institutionName"))
                    binding.qualificationInput.setText(document.getString("qualification"))
                } else {
                    Toast.makeText(requireContext(), "No such document", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(requireContext(), "Error loading profile: ${exception.localizedMessage}", Toast.LENGTH_LONG).show()
            }
    }

    private fun updateUserProfile() {
        val updatedData: MutableMap<String, Any> = hashMapOf(
            "fullName" to binding.fullNameInput.text.toString(),
            //"email" to binding.emailInput.text.toString(),
            "institutionName" to binding.institutionNameInput.text.toString(),
            "qualification" to binding.qualificationInput.text.toString()
        )

        userId?.let {
            // Update Firestore user data
            db.collection("users").document(it).update(updatedData)
                .addOnSuccessListener {
                    Toast.makeText(requireContext(), "Profile updated successfully", Toast.LENGTH_LONG).show()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(requireContext(), "Update failed: ${e.localizedMessage}", Toast.LENGTH_LONG).show()
                }

            // Check if the new password needs to be updated
            val oldPassword = binding.passwordInput1.text.toString()
            val newPassword = binding.passwordInput2.text.toString()

            if (oldPassword.isNotEmpty() && newPassword.isNotEmpty()) {
                // Reauthenticate user to change the password
                val credential = EmailAuthProvider.getCredential(firebaseAuth.currentUser?.email!!, oldPassword)
                firebaseAuth.currentUser?.reauthenticate(credential)
                    ?.addOnCompleteListener { reauthTask ->
                        if (reauthTask.isSuccessful) {
                            firebaseAuth.currentUser?.updatePassword(newPassword)
                                ?.addOnCompleteListener { passwordTask ->
                                    if (passwordTask.isSuccessful) {
                                        Toast.makeText(requireContext(), "Password updated successfully", Toast.LENGTH_SHORT).show()
                                    } else {
                                        Toast.makeText(requireContext(), "Password update failed: ${passwordTask.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                                    }
                                }
                        } else {
                            Toast.makeText(requireContext(), "Reauthentication failed: ${reauthTask.exception?.localizedMessage}", Toast.LENGTH_LONG).show()
                        }
                    }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
