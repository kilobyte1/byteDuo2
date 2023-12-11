package com.example.byteduo.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.adapter.MenuItemsAdapter
import com.example.byteduo.adapter.OrderAdapter
import com.example.byteduo.model.CartItem
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class OrderFragment : Fragment() {
    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var oderAdapter: OrderAdapter
    private lateinit var databaseReference: DatabaseReference



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {

        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_order, container, false)
        //get the recycler from the xml
        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        //link the recycler with the adapter
        cartRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        oderAdapter = OrderAdapter()
        cartRecyclerView.adapter = oderAdapter

        databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")


        return view
    }

}