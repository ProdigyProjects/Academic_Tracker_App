package com.example.academictrackerapp.elvis.flashcards

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import com.example.academictrackerapp.R
import com.example.academictrackerapp.databinding.FragmentFlashcardsBinding
import com.example.academictrackerapp.databinding.FragmentNotificationsBinding
import com.example.academictrackerapp.elvis.data.FlashSet
import com.google.android.material.textfield.TextInputEditText
import com.google.firebase.Firebase
import com.google.firebase.firestore.FirebaseFirestore

class FlashcardsFragment : Fragment() {
        private lateinit var binding: FragmentFlashcardsBinding

        //Variables
        private lateinit var flashTitle: TextInputEditText
        private lateinit var flashDescription: TextInputEditText
        private lateinit var flashQuestion: EditText
        private lateinit var flashAnswer: EditText
        private lateinit var saveButton: Button

        private lateinit var addContainer: LinearLayout
        private lateinit var addButton: ImageButton
        private var questionCount = 0

        private val firestore = FirebaseFirestore.getInstance()



    override fun onCreateView(

        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        binding = FragmentFlashcardsBinding.inflate(inflater, container, false)

        addContainer = binding.container
        addButton = binding.fabAdd

        saveButton.setOnClickListener{
            val title = flashTitle.text.toString().trim()
            val description = flashDescription.text.toString().trim()
            val question = flashQuestion.text.toString().trim()
            val answer = flashAnswer.text.toString().trim()


            if(title.isNotEmpty() || description.isNotEmpty() || question.isNotEmpty() || answer.isNotEmpty()){
                val flashSet = FlashSet(title, description, question, answer)
                saveFlashSetToFirebase(flashSet)
            } else {
                Toast.makeText(requireContext(),"FlashSet Saved!!", Toast.LENGTH_SHORT).show()
            }
        }

        return binding.root
    }

    private fun saveFlashSetToFirebase(flashSet: FlashSet) {
        firestore.collection("flashset")
            .add(flashSet)
            .addOnSuccessListener{
                Toast.makeText(requireContext(), "This information was saved successfully!!", Toast.LENGTH_SHORT).show()
            }
            .addOnFailureListener {
                Toast.makeText(requireContext(), "This information failed saving!!", Toast.LENGTH_SHORT)
            }
    }

    private fun addNewRow() {
        questionCount++  // Increment the question count

        // Inflate a new row item from the XML layout
        val newRow = LayoutInflater.from(requireContext()).inflate(R.layout.row_item_question, addContainer, false)

        // Update the question number
        val textViewQuestionNumber: TextView = newRow.findViewById(R.id.textViewQuestionNumber)
        textViewQuestionNumber.text = "$questionCount."

        // Set up the delete button functionality
        val buttonDelete: ImageButton = newRow.findViewById(R.id.buttonDelete)
        buttonDelete.setOnClickListener {
            addContainer.removeView(newRow)  // Remove this row when the delete button is clicked
            questionCount--  // Decrement the question count
            updateQuestionNumbers()  // Update the question numbers for remaining rows
        }

        // Add the new row to the container
        addContainer.addView(newRow)
    }

    private fun updateQuestionNumbers() {
        for (i in 0 until addContainer.childCount) {
            val row = addContainer.getChildAt(i)
            val textViewQuestionNumber: TextView = row.findViewById(R.id.textViewQuestionNumber)
            textViewQuestionNumber.text = "${i + 1}."  // Update the question numbers based on the current index
        }
    }

}