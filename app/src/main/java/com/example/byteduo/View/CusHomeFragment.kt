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
import com.example.byteduo.model.FirebaseDBManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


class CusHomeFragment : Fragment() {

    private lateinit var menuListView: ListView
    private lateinit var menuAdapter: MenuAdapter
    private lateinit var cartTopCounter: TextView
    private lateinit var userName: TextView

    private val menuItems = listOf("Hot Coffee", "Ice Teas", "Hot Teas", "Bakery", "Drinks")
    private val fragments = listOf(HotCoffeeFragment(), IceTeasFragment(), HotTeasFragment(), BakeryFragment(), DrinksFragment())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_cus_home, container, false)

        // Initialization code
        userName = view.findViewById(R.id.txtUserName)
        val searchBar = view.findViewById<EditText>(R.id.txtSearch)
        cartTopCounter = view.findViewById(R.id.txtCart)

        //get the listview on the xml // ready to contain the various fragments
        menuListView = view.findViewById(R.id.menuListView)
        menuAdapter = MenuAdapter(requireActivity(), menuItems)
        menuListView.adapter = menuAdapter

        // Item click listener
        menuListView.setOnItemClickListener { _, _, position, _ ->
            //replace fragment
            onMenuItemClicked(position)
        }

        // Get the default fragment position
        val defaultPosition = menuAdapter.getDefaultFragmentPosition()

        // Replace the fragment container with the default fragment
        onMenuItemClicked(defaultPosition)

        // Set the selected position in the menu adapter
        menuAdapter.setSelectedPosition(defaultPosition)

        // Set an OnClickListener to enable focus when search is clicked
        searchBar.setOnClickListener {
            searchBar.isFocusableInTouchMode = true
            searchBar.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(searchBar, InputMethodManager.SHOW_IMPLICIT)
        }

        //get the Customer userId and the cart items from the database
        //using call back functions in the database manager
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            updateCustomerInfo(userId)
            updateCartCount(userId)
        }

        return view
    }

    // Function to update the cart counter TextView
    private fun updateCartCount(userId: String) {
        Log.d("CusHomeFragment", "updateCartCount: Getting number of items in cart...")
        // Call the function to get the number of items in the cart
        FirebaseDBManager.getNumberOfItemsInCart(userId) { numberOfItems ->
            // Update your UI with the number of items
            requireActivity().runOnUiThread {
                cartTopCounter.text = numberOfItems.toString()
            }
        }
    }

    // Function to update customer information
    private fun updateCustomerInfo(userId: String) {
        FirebaseDBManager.getCustomerInfo(userId) { customer ->
            if (customer != null) {
                requireActivity().runOnUiThread {
                    val name = customer.username
                    userName.text = name
                }
            }
        }
    }

    // Replace the fragment container with the selected fragment
    private fun onMenuItemClicked(position: Int) {
        requireActivity().supportFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, fragments[position])
            .commit()

        menuAdapter.setSelectedPosition(position)
    }
}


