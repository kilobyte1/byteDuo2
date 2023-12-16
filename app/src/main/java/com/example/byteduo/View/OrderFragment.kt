package com.example.byteduo.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.OrderHandler.Companion
import com.example.byteduo.Controller.OrderHandler.Companion.retrieveOrdersForUser
import com.example.byteduo.R
import com.example.byteduo.View.OrdersAdapter.OrdersAdapter
import com.example.byteduo.model.Order
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference


class OrderFragment : Fragment() {

    private lateinit var ordersAdapter: OrdersAdapter

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
            }
        } else {
            // Handle the case where userId is null (user not authenticated)
            // You may redirect the user to the login screen or handle it based on your app's logic.
        }

        return view
    }

}
