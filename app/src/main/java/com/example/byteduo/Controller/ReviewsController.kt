package com.example.byteduo.Controller

import android.app.AlertDialog
import android.content.Context
import android.widget.Toast
import com.example.byteduo.Model.CustomerReview
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class ReviewsController(private val context: Context) {

    private val reviewsReference: DatabaseReference =
        FirebaseDatabase.getInstance().getReference("reviews")

    fun addReviewAndGenerateResponse(customerId: String, orderId: String, customerName: String, rating: Float, reviewText: String) {
        // Create a CustomerReview object
        val customerReview =
            CustomerReview(customerId,orderId,customerName,rating, reviewText)
        //getCustomerInfo
        // Push the review to the "reviews" node
        val newReviewReference = reviewsReference.push()
        newReviewReference.setValue(customerReview)
            .addOnSuccessListener {
                // Generate and show the response
                 generateResponse(rating, reviewText)
            }
            .addOnFailureListener {
                // Handle failure,
                showToast("Failed to add review: ${it.message}")
            }
    }

    private fun generateResponse(reviewRating: Float, reviewText: String) {
        val responseHeader = "Thank you for your feedback!"

        val responseMessage: String = if (reviewRating >= 4.0) {
            "We're happy to hear that you enjoyed your experience with us."
        } else {
            "We appreciate your feedback and apologize for any inconvenience caused."
        }
        val response = "$responseHeader\n\n$responseMessage\n\nYour Review: $reviewText"

        val alertDialog = AlertDialog.Builder(context)
            .setTitle("Review")
            .setMessage(response)
            .setPositiveButton("OK") { dialog, _ ->
                dialog.dismiss()
            }.create()

        alertDialog.show()
    }

    private fun showToast(message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

}
