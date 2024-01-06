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

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [DrinksFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class DrinksFragment : Fragment() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var databaseReference: DatabaseReference
    private lateinit var adapter: MenuItemsAdapter

    private val menuItems = MenuItems()
    //init cart controller
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
            //directly triggered by the onClick Listener

            cartController.handleAddToCart(menuItem, itemCount)
        },
        onUpdateCartListener = {
            // Handle additional add logic
        })

        recyclerView.adapter = adapter

        Log.d("Drink", "we got here")

        databaseReference = FirebaseDatabase.getInstance().getReference("MenuItems")

        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("Drinks")

        return view
    }

    private fun retrieveAndDisplayItems(category: String) {
        menuItems.retrieveItemsByCategory(category) { items ->
            // Update the adapter with the retrieved items
            adapter.setItems(items)
        }
    }
}