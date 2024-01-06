package com.example.byteduo.View.OrdersAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.Model.CustomerReview

class ReviewsAdapter(private var reviews: List<CustomerReview>) :
    RecyclerView.Adapter<ReviewsAdapter.ReviewViewHolder>() {

    inner class ReviewViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val customerName: TextView = itemView.findViewById(R.id.customerNameTextView)
        val orderId: TextView = itemView.findViewById(R.id.orderIdTextView)
        val rating: TextView = itemView.findViewById(R.id.ratingTextView)
        val reviewText: TextView = itemView.findViewById(R.id.reviewTextTextView)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ReviewViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.review_item_layout, parent, false)
        return ReviewViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: ReviewViewHolder, position: Int) {
        val currentReview = reviews[position]

        // Bind data to the views in the review item layout
        holder.customerName.text = "Customer: ${currentReview.customerName}"
        holder.orderId.text = "Order ID: ${currentReview.orderId}"
        holder.rating.text = "Rating: ${currentReview.rating}"
        holder.reviewText.text = "Review: ${currentReview.reviewText}"
    }

    fun setReviews(reviews: List<CustomerReview>) {
        this.reviews = reviews
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return reviews.size
    }
}
