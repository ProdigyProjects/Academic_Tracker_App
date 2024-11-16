package com.example.academictrackerapp.katiana

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.Imraan.Chatbot.ChatViewModel
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.LinkToPagesBinding
import com.example.academictrackerapp.Imraan.TimeTable.TimetableActivity

import com.example.academictrackerapp.Imraan.Chatbot.MainChatBotctivity  // Import your MainChatBotActivity

class LinkPages : Fragment() {

    private var _binding: LinkToPagesBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = LinkToPagesBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.flashcards.setOnClickListener {
            findNavController().navigate(R.id.toFlashcards)
        }

        binding.goalsSettings.setOnClickListener {
            findNavController().navigate(R.id.toGoalsSettings)
        }

        binding.moreProgress.setOnClickListener {
            findNavController().navigate(R.id.toProgressTracking)
        }

        /*binding.leaderboard.setOnClickListener {
            findNavController().navigate(R.id.toLeaderboard)
        }

        binding.peerCollaboration.setOnClickListener {
            findNavController().navigate(R.id.toPeerCollaboration)
        }*/

        binding.marketplace.setOnClickListener {
            findNavController().navigate(R.id.toMarketplace)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
