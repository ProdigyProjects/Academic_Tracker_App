package com.example.academictrackerapp.katiana

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.Imraan.Chatbot.MainChatBotctivity
import com.example.academictrackerapp.Imraan.grade.MarksActivity
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.LinkToPages2Binding

class LinkPages2 : Fragment() {

    private var _binding: LinkToPages2Binding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = LinkToPages2Binding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.LMS.setOnClickListener {
            findNavController().navigate(R.id.toLMS)
        }

        binding.grade.setOnClickListener {
            val intent = Intent(requireContext(), MarksActivity::class.java)
            startActivity(intent)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
