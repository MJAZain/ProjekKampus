package com.example.projekkampus.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import com.bumptech.glide.Glide
import com.example.projekkampus.EditReviewActivity
import com.example.projekkampus.R
import com.example.projekkampus.models.Review
import com.google.firebase.firestore.FirebaseFirestore

class ReviewAdapter(
    private val context: Context,
    private var reviewList: MutableList<Review>,
    private val documentIds: MutableList<String>, // Store document IDs separately
    private val firestore: FirebaseFirestore
) : BaseAdapter() {
    private val inflater: LayoutInflater = LayoutInflater.from(context)

    override fun getCount(): Int {
        return reviewList.size
    }

    override fun getItem(position: Int): Any {
        return reviewList[position]
    }

    override fun getItemId(position: Int): Long {
        // Use position as the ID since each item is unique in the list
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val itemView = convertView ?: inflater.inflate(R.layout.item_layout, parent, false)

        val review = reviewList[position]

        val imageView = itemView.findViewById<ImageView>(R.id.review_image)
        val titleTextView = itemView.findViewById<TextView>(R.id.review_title)
        val ratingBar = itemView.findViewById<RatingBar>(R.id.review_rating)
        val descriptionTextView = itemView.findViewById<TextView>(R.id.review_description)

        titleTextView.text = review.title
        descriptionTextView.text = review.description
        ratingBar.rating = review.rating

        imageView?.let {
            Glide.with(context).load(review.image).into(it)
        }

        val deleteButton = itemView.findViewById<ImageView>(R.id.delete_button)
        deleteButton?.setOnClickListener {
            deleteReviewFromFirestore(position)
        }

        val editButton = itemView.findViewById<ImageView>(R.id.edit_button)
        editButton?.setOnClickListener {
            val intent = Intent(context, EditReviewActivity::class.java)
            intent.putExtra("Review", review)
            intent.putExtra("documentId", documentIds[position]) // Pass document ID
            context.startActivity(intent)
        }

        return itemView
    }

    private fun deleteReviewFromFirestore(position: Int) {
        val documentId = documentIds[position]
        firestore.collection("Review").document(documentId).delete()
            .addOnSuccessListener {
                reviewList.removeAt(position)
                documentIds.removeAt(position)
                notifyDataSetChanged()
            }
            .addOnFailureListener { exception ->
                // Handle failure to delete review
            }
    }

    fun updateReviews(newReviews: List<Review>, newDocumentIds: List<String>) {
        reviewList.clear()
        reviewList.addAll(newReviews)
        documentIds.clear()
        documentIds.addAll(newDocumentIds)
        notifyDataSetChanged()
    }
}
