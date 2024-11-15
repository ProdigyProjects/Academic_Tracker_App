package com.example.academictrackerapp.katiana.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.lifecycleScope
import com.example.academictrackerapp.databinding.DashboardBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.launch

class Dashboard : Fragment() {

    private var _binding: DashboardBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = DashboardBinding.inflate(inflater, container, false)
        val root: View = binding.root

        /*binding.moreProgress.setOnClickListener {
            findNavController().navigate(R.id.toStudyRecommendations)
        }*/

        speakToAI()

        return root
    }

    private fun speakToAI() {
        val generativeModel = GenerativeModel(
            modelName = "gemini-1.5-flash",
            apiKey = "AIzaSyBqrTtlfMhcGzmvDdWcaEd5_WDy3FYO348"
        )

        val prompt = "Write a personalized Insights about my study progress, just two lines. Like You're most productive on weekdays after 4 PM.\\n• You’ve improved in Math by 10% "

        // Use viewLifecycleOwner.lifecycleScope to safely launch the coroutine
        viewLifecycleOwner.lifecycleScope.launch {
            try {
                val response = generativeModel.generateContent(prompt)
                _binding?.insightsText?.text = response.text // Check if binding is still valid
            } catch (e: Exception) {
                e.printStackTrace() // Log or handle the error appropriately
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null // Avoid memory leaks
    }
}
