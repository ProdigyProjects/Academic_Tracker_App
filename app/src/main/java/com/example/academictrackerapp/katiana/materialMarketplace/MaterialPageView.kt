package com.example.academictrackerapp.katiana.materialMarketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.bumptech.glide.Glide
import com.example.academictrackerapp.databinding.MaterialPageViewBinding

class MaterialPageView : Fragment() {

    private var _binding: MaterialPageViewBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MaterialPageViewBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Retrieve data from the Bundle
        val flashcardName = arguments?.getString("flashcardName")
        val flashcardImage = arguments?.getString("flashcardImage")
        val flashcardDescription = arguments?.getString("flashcardDescription")
        val flashcardPrice = arguments?.getString("flashcardPrice")

        // Set the data to the UI components
        binding.materialTitle.text = flashcardName
        binding.materialDescription.text = flashcardDescription
        binding.materialPrice.text = "Price: $flashcardPrice"

        // Load image using Glide
        Glide.with(this)
            .load(flashcardImage)  // The image URL passed from the previous fragment
            .into(binding.materialImage)

        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
