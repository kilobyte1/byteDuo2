package com.example.byteduo

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.OrderHandler.Companion.retrieveAllOrders
import com.example.byteduo.View.OrdersAdapter.OrdersAdapter
import com.example.byteduo.adapter.ManageOrdersAdapter
import com.example.byteduo.model.Order

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

            retrieveAllOrders(){orders ->
            ordersAdapter = ManageOrdersAdapter(orders)
            recyclerView.adapter = ordersAdapter

        }


        return view
    }
}
