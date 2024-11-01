package com.example.academictrackerapp.katiana

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.LinkToPagesBinding

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

        binding.dashboard.setOnClickListener {
            findNavController().navigate(R.id.toDashboard)
        }

        binding.moreProgress.setOnClickListener {
            findNavController().navigate(R.id.toStudyRecommendations)
        }

        binding.marketplace.setOnClickListener {
            findNavController().navigate(R.id.toMaterialMarket)
        }

        binding.userProfile.setOnClickListener {
            findNavController().navigate(R.id.toProfilePage)
        }

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
