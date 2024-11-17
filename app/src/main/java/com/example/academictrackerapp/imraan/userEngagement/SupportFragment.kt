package com.example.academictrackerapp.Imraan.userEngagement

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.example.academictrackerapp.databinding.FragmentSupportBinding

class SupportFragment : Fragment() {

    private var _binding: FragmentSupportBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSupportBinding.inflate(inflater, container, false)

        // Access views directly via binding
        binding.sendButton.setOnClickListener {
            val username = binding.usernameInput.text.toString()
            val email = binding.emailInput.text.toString()
            val question = binding.questionInput.text.toString()

            if (username.isBlank() || email.isBlank() || question.isBlank()) {
                // Show an error message
                Toast.makeText(requireContext(), "Please fill in all fields", Toast.LENGTH_SHORT).show()
            } else {
                sendEmail(username, email, question)
            }
        }

        return binding.root
    }

    private fun sendEmail(username: String, email: String, question: String) {
        val recipient = "Katianaalmeida48@gmail.com" // priscamu89@gmail.com
        val subject = "Support Request from $username"
        val message = "User: $username\nEmail: $email\nQuestion: $question"

        val intent = Intent(Intent.ACTION_SEND).apply {
            type = "message/rfc822"
            putExtra(Intent.EXTRA_EMAIL, arrayOf(recipient))
            putExtra(Intent.EXTRA_SUBJECT, subject)
            putExtra(Intent.EXTRA_TEXT, message)
        }

        // Start the intent chooser
        startActivity(Intent.createChooser(intent, "Send Email"))

        // Clear the question input box and show a success message
        binding.questionInput.text.clear()
        Toast.makeText(requireContext(), "Your question has been sent successfully!", Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
