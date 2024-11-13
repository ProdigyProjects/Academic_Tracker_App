package com.example.academictrackerapp.katiana.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.example.academictrackerapp.databinding.DashboardBinding
import com.google.ai.client.generativeai.GenerativeModel
import kotlinx.coroutines.MainScope
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

    private fun speakToAI(){
        val generativeModel = GenerativeModel(
            // Specify a Gemini model appropriate for your use case
            modelName = "gemini-1.5-flash",
            // Access your API key as a Build Configuration variable (see "Set up your API key" above)
            apiKey = "AIzaSyBqrTtlfMhcGzmvDdWcaEd5_WDy3FYO348"
        )

        val prompt = "Write a personalized Insights about my study progress, just two lines. Like You're most productive on weekdays after 4 PM.\\n• You’ve improved in Math by 10% "
        MainScope().launch {
            val response = generativeModel.generateContent(prompt)
            print(response.text)
            binding.insightsText.setText(response.text)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}