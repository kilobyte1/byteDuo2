package com.example.byteduo.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.OrderHandler
import com.example.byteduo.R

class ManageOrders : Fragment() {

        private lateinit var recyclerView: RecyclerView
        private lateinit var ordersAdapter: ManageOrdersAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_manage_orders, container, false)

        recyclerView = view.findViewById(R.id.recyclerViewManageOrders)
        recyclerView.layoutManager = LinearLayoutManager(context)
        OrderHandler.retrieveAllOrders() { orders ->
            ordersAdapter = ManageOrdersAdapter(orders)
            recyclerView.adapter = ordersAdapter

        }
        return view
    }
}