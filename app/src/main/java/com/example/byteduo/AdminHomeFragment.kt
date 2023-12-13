package com.example.byteduo

import android.content.Context
import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.TextView
import com.example.byteduo.View.items.BakeryFragment
import com.example.byteduo.View.items.DrinksFragment
import com.example.byteduo.View.items.HotCoffeeFragment
import com.example.byteduo.View.items.HotTeasFragment
import com.example.byteduo.View.items.IceTeasFragment
import com.example.byteduo.adapter.MenuAdapter
import com.example.byteduo.model.Admin
import com.example.byteduo.model.Customer
import com.example.byteduo.model.FirebaseDBManager
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener



class AdminHomeFragment : Fragment() {


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
        val view = inflater.inflate(R.layout.fragment_admin_home, container, false)

        // Initialization code
        val userName = view.findViewById<TextView>(R.id.txtUserName)
        val search = view.findViewById<EditText>(R.id.txtSearch)
        val  cartTopCounter = view.findViewById<TextView>(R.id.txtCart)



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


        // Set an OnClickListener to enable focus when search is clicked
        search.setOnClickListener { search.isFocusableInTouchMode = true
            search.requestFocus()
            val imm = requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.showSoftInput(search, InputMethodManager.SHOW_IMPLICIT)
        }


        //get the admin userId from the database
        val userId = FirebaseAuth.getInstance().currentUser?.uid

        if (userId != null) {
            FirebaseDBManager.getAdminInfo(userId) { admin ->
                if (admin != null) {
                    requireActivity().runOnUiThread {
                        val name = admin.username
                        userName.text = name
                    }
                    // Call the function to get the number of items in the cart
                    FirebaseDBManager.getNumberOfItemsInCart(userId) { numberOfItems ->
                        // Update your UI with the number of items
                        requireActivity().runOnUiThread {
                            cartTopCounter.text = numberOfItems.toString()
                        }
                    }
                }
            }
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

}




