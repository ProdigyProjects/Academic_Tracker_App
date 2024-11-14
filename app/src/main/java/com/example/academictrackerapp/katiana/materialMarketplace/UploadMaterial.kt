package com.example.academictrackerapp.katiana.materialMarketplace

import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.fragment.app.Fragment
import com.example.academictrackerapp.databinding.UploadMaterialBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage

class UploadMaterial : Fragment() {

    private var _binding: UploadMaterialBinding? = null
    private val binding get() = _binding!!
    private var imageUri: Uri? = null
    private var fileUri: Uri? = null

    private val selectImageLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            imageUri = result.data?.data
            binding.materialImage.setImageURI(imageUri)
        } else {
            Toast.makeText(requireContext(), "No image selected", Toast.LENGTH_SHORT).show()
        }
    }

    private val selectFileLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            fileUri = result.data?.data
            Toast.makeText(requireContext(), "File selected: ${fileUri?.path}", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(requireContext(), "No file selected", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = UploadMaterialBinding.inflate(inflater, container, false)
        val root: View = binding.root

        binding.floatingActionButton.setOnClickListener {
            openImagePicker()
        }

        binding.buttonUpload.setOnClickListener {
            openFilePicker()
        }

        binding.buttonSubmit.setOnClickListener {
            uploadDataToFirestore()
        }

        return root
    }

    private fun openImagePicker() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        val chooser = Intent.createChooser(intent, "Select an Image")
        selectImageLauncher.launch(chooser)
    }

    private fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            type = "*/*"
            addCategory(Intent.CATEGORY_OPENABLE)
        }
        val chooser = Intent.createChooser(intent, "Select a File")
        selectFileLauncher.launch(chooser)
    }

    private fun uploadDataToFirestore() {
        val title = binding.editTextTitle.text.toString()
        val description = binding.editTextDescription.text.toString()
        val faculty = binding.editTextSubject.text.toString()
        val price = binding.editTextPrice.text.toString()

        if (title.isEmpty() || description.isEmpty() || faculty.isEmpty() || price.isEmpty()) {
            Toast.makeText(requireContext(), "Please fill all fields", Toast.LENGTH_SHORT).show()
            return
        }

        val firestore = FirebaseFirestore.getInstance()
        val storage = FirebaseStorage.getInstance().reference

        // Upload image
        imageUri?.let {
            val imageRef = storage.child("images/${System.currentTimeMillis()}.jpg")
            imageRef.putFile(it).addOnSuccessListener { imageSnapshot ->
                imageRef.downloadUrl.addOnSuccessListener { imageUrl ->
                    // Upload file
                    fileUri?.let { file ->
                        val fileRef = storage.child("files/${System.currentTimeMillis()}")
                        fileRef.putFile(file).addOnSuccessListener { fileSnapshot ->
                            fileRef.downloadUrl.addOnSuccessListener { fileUrl ->
                                // Save material info in Firestore
                                val materialData = hashMapOf(
                                    "title" to title,
                                    "description" to description,
                                    "faculty" to faculty,
                                    "price" to price,
                                    "imageUrl" to imageUrl.toString(),
                                    "fileUrl" to fileUrl.toString()
                                )

                                firestore.collection("materials")
                                    .add(materialData)
                                    .addOnSuccessListener {
                                        Toast.makeText(requireContext(), "Material uploaded successfully", Toast.LENGTH_SHORT).show()
                                    }
                                    .addOnFailureListener {
                                        Toast.makeText(requireContext(), "Failed to upload material", Toast.LENGTH_SHORT).show()
                                    }
                            }
                        }
                    }
                }
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
