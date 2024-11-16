package com.example.academictrackerapp.katiana.materialMarketplace

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.bumptech.glide.Glide
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.MaterialMarketplaceBinding
import com.google.firebase.firestore.FirebaseFirestore

class MaterialMarketplace : Fragment() {

    private var _binding: MaterialMarketplaceBinding? = null
    private val binding get() = _binding!!
    private val db = FirebaseFirestore.getInstance()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = MaterialMarketplaceBinding.inflate(inflater, container, false)
        val root: View = binding.root

        // Set onClickListeners for the buttons
        binding.btnCreateContent.setOnClickListener {
            findNavController().navigate(R.id.toUploadMaterial)
        }

        // Call method to load the flashcards
        loadFlashcards()

        return root
    }

    // Method to load flashcards from Firestore
    private fun loadFlashcards() {
        // Reference to the Firestore collection where flashcards are stored
        db.collection("flashcards")
            .get()
            .addOnSuccessListener { result ->
                // Clear any existing views in GridLayout before adding new ones
                binding.gridLayout.removeAllViews()

                // Loop through each document in Firestore result
                for (document in result) {
                    val flashcardName = document.getString("name") ?: "Unknown"
                    val flashcardImage = document.getString("imageUrl") // Assuming you store image URL
                    val flashcardDescription = document.getString("description") ?: "No description"
                    val flashcardPrice = document.getString("price") ?: "No price"

                    // Create a new LinearLayout for each flashcard
                    val flashcardLayout = LinearLayout(context)
                    flashcardLayout.orientation = LinearLayout.VERTICAL
                    flashcardLayout.layoutParams = LinearLayout.LayoutParams(
                        0, LinearLayout.LayoutParams.WRAP_CONTENT, 1f
                    )
                    flashcardLayout.setBackgroundResource(R.drawable.edit_text_background)
                    flashcardLayout.gravity = android.view.Gravity.CENTER
                    flashcardLayout.setPadding(8, 8, 8, 8)

                    // Create an ImageView for the flashcard image
                    val imageView = ImageView(context)

                    // Use Glide to load the image into the ImageView
                    Glide.with(this)
                        .load(flashcardImage)  // Image URL from Firestore
                        .into(imageView)  // Load image into ImageView

                    // Create a TextView for the flashcard title
                    val textView = TextView(context)
                    textView.text = flashcardName
                    textView.textSize = 12f
                    textView.setPadding(0, 4, 0, 0)

                    // Add the ImageView and TextView to the flashcard layout
                    flashcardLayout.addView(imageView)
                    flashcardLayout.addView(textView)

                    // Add the flashcard layout to the GridLayout
                    binding.gridLayout.addView(flashcardLayout)

                    // Handle flashcard click to navigate to MaterialPageView
                    flashcardLayout.setOnClickListener {
                        val bundle = Bundle().apply {
                            putString("flashcardName", flashcardName)
                            putString("flashcardImage", flashcardImage)
                            putString("flashcardDescription", flashcardDescription)
                            putString("flashcardPrice", flashcardPrice)
                        }
                        findNavController().navigate(R.id.toViewMaterialPage, bundle)
                    }
                }
            }
            .addOnFailureListener { exception ->
                println("Error getting documents: $exception")
            }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
