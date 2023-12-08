package com.example.byteduo.View.items

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.adapter.MenuItemsAdapter
import com.example.byteduo.model.MenuItems
import com.google.firebase.database.*

class BakeryFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: MenuItemsAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_bakery, container, false)
        recyclerView = view.findViewById(R.id.recyclerView)
        recyclerView.layoutManager = LinearLayoutManager(requireContext())
        adapter = MenuItemsAdapter()
        recyclerView.adapter = adapter

        Log.d("Bakery", "we got here")

        databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")

        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("bakery")

        return view
    }

    private fun retrieveAndDisplayItems(category: String) {
        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val items = mutableListOf<MenuItems>()

                for (itemSnapshot in snapshot.children) {
                    val menuItem = itemSnapshot.getValue(MenuItems::class.java)
                    if (menuItem != null && menuItem.category == category) {
                        items.add(menuItem)
                    }
                }

                // Update the adapter with the filtered items
                adapter.setItems(items)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }
}