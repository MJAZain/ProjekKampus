package com.example.projekkampus

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekkampus.databinding.ActivityAddReviewBinding
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.storage.FirebaseStorage
import java.io.IOException
import java.util.UUID

class AddReview : AppCompatActivity() {
    private lateinit var binding: ActivityAddReviewBinding
    private val firestore = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()
    private val PICK_IMAGE_REQUEST = 1
    private var selectedImageUri: Uri? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAddReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.reviewImage.setOnClickListener {
            openGallery()
        }

        binding.submitReviewButton.setOnClickListener {
            val title = binding.reviewTitle.text.toString()
            val rating = binding.reviewRating.rating
            val description = binding.reviewDescription.text.toString()

            if (title.isBlank() || description.isBlank()) {
                showToast("Title and description are required")
            } else {
                if (selectedImageUri != null) {
                    uploadImageAndSaveReview(title, rating, description)
                } else {
                    saveReviewToFirestore(title, rating, description, null)
                }
            }
        }

        binding.closeButton.setOnClickListener {
            finish()
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, PICK_IMAGE_REQUEST)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == Activity.RESULT_OK) {
            selectedImageUri = data?.data
            selectedImageUri?.let {
                try {
                    val bitmap: Bitmap = MediaStore.Images.Media.getBitmap(this.contentResolver, it)
                    binding.reviewImage.setImageBitmap(bitmap)
                } catch (e: IOException) {
                    showToast("Failed to load image: ${e.localizedMessage}")
                }
            }
        }
    }

    private fun uploadImageAndSaveReview(title: String, rating: Float, description: String) {
        val imageName = "review_images/${UUID.randomUUID()}.jpg"
        val storageRef = storage.reference.child(imageName)

        storageRef.putFile(selectedImageUri!!)
            .addOnSuccessListener { taskSnapshot ->
                taskSnapshot.storage.downloadUrl.addOnSuccessListener { imageUrl ->
                    saveReviewToFirestore(title, rating, description, imageUrl.toString())
                }
            }
            .addOnFailureListener { exception ->
                showToast("Failed to upload image: ${exception.localizedMessage}")
            }
    }

    private fun saveReviewToFirestore(title: String, rating: Float, description: String, imageUrl: String?) {
        val reviewData = hashMapOf(
            "title" to title,
            "rating" to rating,
            "description" to description
        )

        imageUrl?.let {
            reviewData["image"] = it
        }

        firestore.collection("Review").add(reviewData)
            .addOnSuccessListener { documentReference ->
                showToast("Review submitted successfully")
                finish()
            }
            .addOnFailureListener { exception ->
                showToast("Submission failed: ${exception.localizedMessage}")
            }
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }
}
