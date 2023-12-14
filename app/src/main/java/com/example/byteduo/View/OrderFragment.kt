package com.example.byteduo.View

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.google.firebase.database.DatabaseReference


class OrderFragment : Fragment() {
    private lateinit var ordersRecyclerView: RecyclerView



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
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        //get the recycler from the xml
        ordersRecyclerView = view.findViewById(R.id.ordersRecyclerView)



        return view
    }

}