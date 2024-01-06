package com.example.byteduo.View

import android.app.AlertDialog
import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.RatingBar
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.OrderHandler.Companion.retrieveOrdersForUser
import com.example.byteduo.Controller.ReviewsController
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.Model.FirebaseDBManager.getCurrentUserId
import com.example.byteduo.R
import com.example.byteduo.View.OrdersAdapter.OrdersAdapter
import com.google.firebase.auth.FirebaseAuth


class OrderFragment : Fragment() {

    private lateinit var ordersAdapter: OrdersAdapter
    private val PREFS_NAME = "ReviewDialogPrefs"
    private val REVIEW_DIALOG_SHOWN_KEY = "ReviewDialogShown"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)

        val recyclerView: RecyclerView = view.findViewById(R.id.ordersRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())

        val userId = FirebaseAuth.getInstance().currentUser?.uid
        if (userId != null) {
            retrieveOrdersForUser(userId) { orders ->
                // Initialize and set the adapter with the retrieved orders
                ordersAdapter = OrdersAdapter(orders)
                recyclerView.adapter = ordersAdapter

                if (orders.isNotEmpty() && orders.last().orderStatus == "Collected") {
                    val mostRecentOrder = orders.last()
                    // Check if the review dialog has been shown for this specific order
                    //dialog should only show when last order has been collected
                    if (!mostRecentOrder.orderId?.let { isReviewDialogShown(it) }!!) {
                        mostRecentOrder.orderId?.let { showReviewDialog(it) }
                        mostRecentOrder.orderId?.let { markReviewDialogAsShown(it) }
                    }
                }
            }
        }
        return view
    }

    private fun showReviewDialog(orderId: String) {
        val dialogView = layoutInflater.inflate(R.layout.dialog_review_default_orderid, null)
        val etReview = dialogView.findViewById<EditText>(R.id.etReview)
        val ratingBar = dialogView.findViewById<RatingBar>(R.id.ratingBar)
        Log.d("OrderFragment", "Showing review dialog for order: $orderId")

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Review")
            .setMessage("Would you like to leave a review for your most recent collected order?")
            .setView(dialogView)
            .setPositiveButton("Submit") { _, _ ->
                // Retrieve user input from the dialog and process
                val reviewText = etReview.text.toString()
                if (reviewText.isNotEmpty()) {
                    // Process the review text
                    val reviewsController = ReviewsController(requireContext())
                    val customerId = getCurrentUserId()

                    if (customerId != null) {
                        FirebaseDBManager.getCustomerInfo(customerId) { customer ->
                            requireActivity().runOnUiThread {
                                val name = customer?.fullName
                                if (name != null) {
                                    val rating = ratingBar.rating
                                    reviewsController.addReviewAndGenerateResponse(customerId, orderId,name, rating, reviewText)
                                } else {
                                    Log.d("Review", "Customer name not available")
                                }
                            }
                        }
                    } else {
                        // If not a customer, fetch admin information
                        val adminId = getCurrentUserId()
                        if (adminId != null) {
                            FirebaseDBManager.getAdminInfo(adminId) { admin ->
                                requireActivity().runOnUiThread {
                                    val name = admin?.fullName

                                    if (name != null) {
                                        val rating = ratingBar.rating
                                        reviewsController.addReviewAndGenerateResponse(adminId,orderId, name, rating, reviewText)
                                    } else {
                                        Log.d("Review", "Admin name not available")
                                    }
                                }
                            }
                        } else {
                            // Handle the case when both customer and admin IDs are null
                            Log.d("Review", "Customer and Admin IDs are null")
                        }
                    }
                }
            }
            .setNegativeButton("No") { dialog, _ ->
                dialog.dismiss()
            }.create()

        dialog.show()
    }


    private fun isReviewDialogShown(orderId: String): Boolean {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        val isShown = prefs.getBoolean("$REVIEW_DIALOG_SHOWN_KEY$orderId", false)
        Log.d("OrderFragment", "Review dialog shown for order $orderId: $isShown")
        return isShown
    }

    private fun markReviewDialogAsShown(orderId: String) {
        val prefs = requireContext().getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
        prefs.edit().putBoolean("$REVIEW_DIALOG_SHOWN_KEY$orderId", true).apply()
        Log.d("OrderFragment", "Marking review dialog as shown for order: $orderId")
    }

}
