package com.example.projekkampus

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.projekkampus.adapters.ReviewAdapter
import com.example.projekkampus.databinding.ActivityMainContentBinding
import com.example.projekkampus.models.Review
import com.google.firebase.firestore.FirebaseFirestore

class MainContent : AppCompatActivity() {
    private lateinit var binding: ActivityMainContentBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var firestore: FirebaseFirestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainContentBinding.inflate(layoutInflater)
        setContentView(binding.root)

        firestore = FirebaseFirestore.getInstance()

        // Initialize the adapter and set it to the GridView
        reviewAdapter = ReviewAdapter(this, mutableListOf(), mutableListOf(), firestore)
        binding.reviewGridView.adapter = reviewAdapter

        // Set the FAB to add a new review
        binding.addFab.setOnClickListener {
            val intent = Intent(this, AddReview::class.java)
            startActivity(intent)
        }

        // Set the FAB to refresh the data from Firestore
        binding.refreshFab.setOnClickListener {
            loadReviewsFromFirestore()
        }

        // Load reviews from Firestore when the activity starts
        loadReviewsFromFirestore()
    }

    private fun loadReviewsFromFirestore() {
        firestore.collection("Review")
            .get()
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    // Fetch the reviews and update the adapter
                    val reviews = task.result?.toObjects(Review::class.java) ?: emptyList()
                    val documentIds = task.result?.documents?.map { it.id } ?: emptyList()
                    reviewAdapter.updateReviews(reviews, documentIds)
                    Log.d("MainContent", "Fetched ${reviews.size} reviews")
                } else {
                    // Handle failure with a log and toast
                    Log.e("MainContent", "Error fetching reviews", task.exception)
                    Toast.makeText(this, "Error fetching reviews", Toast.LENGTH_LONG).show()
                }
            }
    }
}
