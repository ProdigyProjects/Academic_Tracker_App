package com.example.academictrackerapp.katiana.materialMarketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.MaterialMarketplaceBinding

class MaterialMarketplace : Fragment() {

    private var _binding: MaterialMarketplaceBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = MaterialMarketplaceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.flashcardSelected.setOnClickListener {
            //findNavController().navigate(R.id.toUploadMaterial)
        }
        binding.paidMaterialSelected.setOnClickListener {
            findNavController().navigate(R.id.toViewMaterialPage)
        }
        binding.btnCreateContent.setOnClickListener {
            findNavController().navigate(R.id.toUploadMaterial)
        }
        binding.recommendedMaterialSelected.setOnClickListener {
            findNavController().navigate(R.id.toViewMaterialPage)
        }
        binding.trendingMaterialSelected.setOnClickListener {
            findNavController().navigate(R.id.toViewMaterialPage)
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}