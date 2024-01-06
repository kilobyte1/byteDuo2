package com.example.byteduo.View.items

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.CartController
import com.example.byteduo.R
import com.example.byteduo.View.MenuItemsAdapter
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.Model.MenuItems
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class HotCoffeeFragment : Fragment() {


    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: MenuItemsAdapter

    private val menuItems = MenuItems()
    private val cartController = CartController()

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

        }
        )
        recyclerView.adapter = adapter

        Log.d("HotCoffee", "we got here")

        databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")

        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("Hot Coffee")

        return view
    }

    private fun retrieveAndDisplayItems(category: String) {
        menuItems.retrieveItemsByCategory(category) { items ->
            // Update the adapter with the retrieved items
            adapter.setItems(items)
        }
    }
    override fun onResume() {
        super.onResume()
        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("Hot Coffee")
    }
}