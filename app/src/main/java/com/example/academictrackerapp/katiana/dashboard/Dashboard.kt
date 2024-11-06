package com.example.academictrackerapp.katiana.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.DashboardBinding

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

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}