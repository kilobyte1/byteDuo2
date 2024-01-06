package com.example.byteduo.View.items

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.CartController
import com.example.byteduo.R
import com.example.byteduo.View.MenuItemsAdapter
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.Model.MenuItems
import com.google.firebase.database.*

class BakeryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: MenuItemsAdapter

    private val menuItems = MenuItems()
    private val cartController= CartController()
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bakery, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MenuItemsAdapter ({ menuItem,itemCount ->
            // Handle the "Add" button click here
            cartController.handleAddToCart(menuItem, itemCount)
        },
        onUpdateCartListener = {
            // Handle cart update here
        })

        recyclerView.adapter = adapter

        Log.d("Bakery", "we got here")

        databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")

        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("Bakery")

        return view
    }

    private fun retrieveAndDisplayItems(category: String) {
        menuItems.retrieveItemsByCategory(category) { items ->
            // Update the adapter with the retrieved items
            adapter.setItems(items)
        }
    }
}
