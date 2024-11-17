package com.example.academictrackerapp.elvis.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val registrationViewModel =
            ViewModelProvider(this).get(HomeViewModel::class.java)

        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val root: View = binding.root
/*
        val textView: TextView = binding.textHome
        registrationViewModel.text.observe(viewLifecycleOwner) {
            textView.text = it
        }*/

        // Use NavController to navigate to LoginFragment and RegistrationFragment
        binding.buttonLogin.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_loginFragment)
        }

        binding.buttonRegister.setOnClickListener {
            findNavController().navigate(R.id.action_homeFragment_to_registrationFragment)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
