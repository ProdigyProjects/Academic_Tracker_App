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

class RegistrationFragment : Fragment() {

    private var _binding: FragmentRegistrationBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registrationViewModel =
            ViewModelProvider(this).get(RegistrationViewModel::class.java)

        _binding = FragmentRegistrationBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.registerButton.setOnClickListener {
            val profileImage = binding.profileImage.imageAlpha.toString()
            val email = binding.emailInput.text.toString()
            val institutionName = binding.institutionNameInput.text.toString()
            val qualification = binding.qualificationInput.text.toString()
            val password = binding.passwordInput.text.toString()

            if(email.isNotEmpty() && password.isNotEmpty()) {
                MainActivity.auth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener {
                        if(it.isSuccessful) {
                            findNavController().navigate(R.id.action_registrationFragment_to_loginFragment)
                        }
                    }.addOnFailureListener {
                        Toast.makeText(requireContext(), it.localizedMessage, Toast.LENGTH_LONG).show()
                    }
            }
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
