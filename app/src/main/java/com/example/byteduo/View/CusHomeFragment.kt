package com.example.byteduo.View

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.byteduo.R
import com.example.byteduo.View.items.BakeryFragment
import com.example.byteduo.View.items.DrinksFragment
import com.example.byteduo.View.items.HotCoffeeFragment
import com.example.byteduo.View.items.HotTeasFragment
import com.example.byteduo.View.items.IceTeasFragment
import com.example.byteduo.adapter.MenuAdapter
import com.example.byteduo.model.Customer
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CusHomeFragment : Fragment() {

    private lateinit var databaseReference: DatabaseReference


    private lateinit var menuListView: ListView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var cartTopCounter: TextView

    private val menuItems = listOf("Hot Coffee","Ice Teas","Hot Teas", "Bakery", "Drinks")
    private val fragments = listOf(HotCoffeeFragment(), IceTeasFragment(), HotTeasFragment(), BakeryFragment(), DrinksFragment())



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
        val view = inflater.inflate(R.layout.fragment_cus_home, container, false)

        // Initialization code
        val userName = view.findViewById<TextView>(R.id.txtUserName)
        val searchBar = view.findViewById<EditText>(R.id.txtSearch)
        cartTopCounter = view.findViewById<TextView>(R.id.txtCart)

        //get the listview on the xml // ready to contain the various fragments
        menuListView = view.findViewById(R.id.menuListView)
        menuAdapter = MenuAdapter(requireActivity(), menuItems)
        menuListView.adapter = menuAdapter

        // Item click listener
        menuListView.setOnItemClickListener { _, _, position, _ ->
            //replace fragment
            onMenuItemClicked(position)
        }


        // Create an instance of MenuAdapter
        menuAdapter = MenuAdapter(requireActivity(), menuItems)
        menuListView.adapter = menuAdapter

        // Get the default fragment position
        val defaultPosition = menuAdapter.getDefaultFragmentPosition()

        // Replace the fragment container with the default fragment
        onMenuItemClicked(defaultPosition)

        // Set the selected position in the menu adapter
        menuAdapter.setSelectedPosition(defaultPosition)

        // Call the function to get the number of items in the cart
        updateCartCount()




        // Set an OnClickListener to enable focus when search is clicked
        searchBar.setOnClickListener { searchBar.isFocusableInTouchMode = true
            searchBar.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            val database = FirebaseDatabase.getInstance().reference
            val customerReference = database.child("customers").child(userId)

            customerReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("SnapshotData", snapshot.toString()) // Log the snapshot data

                    if (snapshot.exists()) {
                        val customer = snapshot.getValue(Customer::class.java)
                        Log.d("User", "customer is $customer")
                        if (customer != null) {
                            requireActivity().runOnUiThread {
                                val name = customer.username
                                userName.text = name
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }


        return view
    }

    // Replace the fragment container with the selected fragment
    private fun onMenuItemClicked(position: Int) {

        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragments[position])
            .commit()

        menuAdapter.setSelectedPosition(position)
    }

    // Function to update the cart counter TextView

    private fun updateCartCount() {
        Log.d("CusHomeFragment", "updateCartCount: Getting number of items in cart...")

        getNumberOfItemsInCart { numberOfItems ->
            // Update the UI with the number of items
            Log.d("CusHomeFragment", "updateCartCount: Number of items in cart: $numberOfItems")

            requireActivity().runOnUiThread {
                cartTopCounter.text = numberOfItems.toString()
            }
        }
    }

    // Function to get the number of items in the cart
    private fun getNumberOfItemsInCart(callback: (Int) -> Unit) {
        Log.d("CusHomeFragment", "getNumberOfItemsInCart: Fetching number of items in cart...")

        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            databaseReference.addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Sum up the quantities of all items in the cart
                    var totalQuantity = 0

                    for (itemSnapshot in snapshot.children) {
                        val quantity = (itemSnapshot.child("quantity").value as? Long)?.toInt() ?: 0
                        totalQuantity += quantity
                    }

                    Log.d("CusHomeFragment", "getNumberOfItemsInCart: Total quantity in cart: $totalQuantity")

                    callback(totalQuantity)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("CusHomeFragment", "getNumberOfItemsInCart: Error fetching items in cart", error.toException())

                    // Handle error
                    callback(0) // Return 0 in case of an error
                }
            })
        } else {
            // User not authenticated, return 0 items
            callback(0)
        }
    }




}