package com.example.projekkampus

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekkampus.databinding.ActivityEditReviewBinding
import com.example.projekkampus.models.Review
import com.google.firebase.firestore.FirebaseFirestore

class EditReviewActivity : AppCompatActivity() {
    private lateinit var binding: ActivityEditReviewBinding
    private lateinit var firestore: FirebaseFirestore
    private lateinit var review: Review
    private var documentId: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Get the review and document ID from the intent, with proper error handling
        review = intent.getSerializableExtra("Review") as? Review ?: run {
            Toast.makeText(this, "Invalid review data", Toast.LENGTH_SHORT).show()
            finish()
            return
        }

        documentId = intent.getStringExtra("documentId")

        if (documentId.isNullOrEmpty()) {
            Toast.makeText(this, "Invalid document ID", Toast.LENGTH_SHORT).show()
            Log.e("EditReviewActivity", "Received null or empty document ID")
            finish()
            return
        }

        // Load the document to verify it exists
        firestore.collection("Review").document(documentId!!).get()
            .addOnSuccessListener { document ->
                if (!document.exists()) {
                    Toast.makeText(this, "Document does not exist", Toast.LENGTH_SHORT).show()
                    Log.e("EditReviewActivity", "Document with ID $documentId does not exist")
                    finish()
                } else {
                    // Set up the fields for editing with retrieved review data
                    binding.editReviewTitle.setText(review.title)
                    binding.editReviewDescription.setText(review.description)
                    binding.editReviewRating.rating = review.rating

                    // Set the Save button to update the Firestore document
                    binding.saveReviewButton.setOnClickListener {
                        val newTitle = binding.editReviewTitle.text.toString()
                        val newDescription = binding.editReviewDescription.text.toString()
                        val newRating = binding.editReviewRating.rating

                        if (newTitle.isBlank() || newDescription.isBlank()) {
                            Toast.makeText(this, "Title and description cannot be empty", Toast.LENGTH_SHORT).show()
                            return@setOnClickListener
                        }

                        // Update the Firestore document with the correct document ID
                        val updatedReview = review.copy(
                            title = newTitle,
                            description = newDescription,
                            rating = newRating
                        )
                        updateReviewToFirestore(updatedReview, documentId!!)
                    }

                    // Close the activity when the close button is clicked
                    binding.closeButton.setOnClickListener {
                        finish()
                    }
                }
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to verify document: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("EditReviewActivity", "Error fetching document", exception)
                finish()
            }
    }

    private fun updateReviewToFirestore(review: Review, documentId: String) {
        firestore.collection("Review").document(documentId).set(review)
            .addOnSuccessListener {
                Toast.makeText(this, "Review updated successfully", Toast.LENGTH_SHORT).show()
                finish()
            }
            .addOnFailureListener { exception ->
                Toast.makeText(this, "Failed to update review: ${exception.localizedMessage}", Toast.LENGTH_SHORT).show()
                Log.e("EditReviewActivity", "Error updating review", exception)
            }
    }
}
