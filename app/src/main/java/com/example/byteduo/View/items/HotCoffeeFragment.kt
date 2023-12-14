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
import com.example.byteduo.R
import com.example.byteduo.adapter.MenuItemsAdapter
import com.example.byteduo.model.CartItem
import com.example.byteduo.model.FirebaseDBManager
import com.example.byteduo.model.MenuItems
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
 * Use the [HotCoffeeFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class HotCoffeeFragment : Fragment() {


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
        adapter = MenuItemsAdapter ({ menuItem,itemCount ->
            // Handle the "Add" button click here
            handleAddToCart(menuItem, itemCount)
        },
        onUpdateCartListener = {
            // Handle cart update here if needed
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
        databaseReference.addListenerForSingleValueEvent(object : ValueEventListener {
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


    // Handle the "Add" button click by creating a CartItem and storing it in the database
    private fun handleAddToCart(menuItem: MenuItems,quantity: Int) {
        if (quantity > 0) {
            // Create a CartItem object with the necessary details
            val cartItem = CartItem(
                menuItem = menuItem,
                quantity = quantity,
            )

            // Store the cartItem in the database or perform other actions as needed
            storeCartItemInDatabase(cartItem)
            Toast.makeText(requireContext(), "Item added", Toast.LENGTH_SHORT).show()

        } else {
            // Show a toast message if quantity is zero
            Toast.makeText(requireContext(), "Quantity cannot be 0", Toast.LENGTH_SHORT).show()
        }
    }
    private fun storeCartItemInDatabase(cartItem: CartItem) {

        val userId = FirebaseDBManager.getCurrentUserId()

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            databaseReference.push().setValue(cartItem)
        }
    }

    override fun onResume() {
        super.onResume()
        // Fetch data from Firebase and filter items with category
        retrieveAndDisplayItems("Hot Coffee")
    }
}