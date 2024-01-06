package com.example.byteduo.Model

import android.content.Context
import android.util.Log
import android.widget.Toast
import com.example.byteduo.Controller.Loading
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener


object FirebaseDBManager {

    // Reference to the root of the Firebase Realtime Database
    private val databaseReference = FirebaseDatabase.getInstance().reference
    // Add customer information to the database
    fun addCustomer(customer: Customer) {
        val userId = FirebaseAuth.getInstance().currentUser?.uid
        userId?.let {
            // Reference to the "customers" node using the user's unique ID
            val userReference = databaseReference.child("customers").child(it)
            userReference.child("email").setValue(customer.email)
            userReference.child("fullName").setValue(customer.fullName)
            userReference.child("username").setValue(customer.username)
            userReference.child("mobile").setValue(customer.mobile)
            userReference.child("role").setValue(customer.role)
            userReference.child("active").setValue(customer.active)
        }
    }

    // Get the current user's ID using
    fun getCurrentUserId(): String? {
        val auth = FirebaseAuth.getInstance()
        val currentUser = auth.currentUser
        return currentUser?.uid
    }
    // Retrieve admin information based on the provided user ID
    fun getAdminInfo(userId: String, callback: (Admin?) -> Unit) {
        val adminReference = FirebaseDatabase.getInstance().getReference("admins").child(userId)
        adminReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val admin = snapshot.getValue(Admin::class.java)
                    callback(admin)
                } else {
                    callback(null)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback(null)
            }
        })
    }

    // Retrieve customer information based on the provided user ID
    fun getCustomerInfo(userId: String, callback: (Customer?) -> Unit) {
        val customerReference = FirebaseDatabase.getInstance().getReference("customers").child(userId)
        customerReference.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {
                    val customers = snapshot.getValue(Customer::class.java)
                    callback(customers)
                } else {
                    callback(null)
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle the error
                callback(null)
            }
        })
    }
    // Get the total number of items in the user's cart
    fun getNumberOfItemsInCart(userId: String, callback: (Int) -> Unit) {
        val databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

        databaseReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Sum up the quantities of all items in the cart
                var totalQuantity = 0
                for (itemSnapshot in snapshot.children) {
                    // Get quantity from each item in the cart
                    val quantity = (itemSnapshot.child("quantity").value as? Long)?.toInt() ?: 0
                    totalQuantity += quantity
                }
                callback(totalQuantity)
            }
            override fun onCancelled(error: DatabaseError) {
                callback(0) // Return 0 in case of an error
            }
        })
    }

    // Get the list of menu items from the "MenuItems" node in the database
    fun getMenuItems(callback: (List<MenuItems>) -> Unit) {
        try {
            // Initialize Firebase Database
            val menuItemsRef = FirebaseDatabase.getInstance().getReference("MenuItems")
            // Fetch menu items from the database
            menuItemsRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(dataSnapshot: DataSnapshot) {
                    val menuItems = mutableListOf<MenuItems>()
                    for (itemSnapshot in dataSnapshot.children) {
                        val key = itemSnapshot.key
                        val menuItem = itemSnapshot.getValue(MenuItems::class.java)?.copy(itemId = key)
                        menuItem?.let { menuItems.add(it) }
                    }
                    // Invoke the callback with the menu items
                    callback(menuItems)
                }

                override fun onCancelled(databaseError: DatabaseError) {
                    // Handle the error
                }
            })

        } catch (e: Exception) {
            // Print stack trace in case of an exception
            e.printStackTrace()
        }
    }

    // Clear items from the user's cart based on the provided user ID
    fun clearUserCart(userId: String) {
        val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)
        cartReference.removeValue()
    }

    fun getAllReviews(callback: (List<CustomerReview>) -> Unit) {
        val reviewsRef: DatabaseReference = databaseReference.child("reviews")

        val reviewsList: MutableList<CustomerReview> = mutableListOf()

        reviewsRef.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                for (reviewSnapshot in dataSnapshot.children) {
                    val review = reviewSnapshot.getValue(CustomerReview::class.java)
                    if (review != null) {
                        reviewsList.add(review)
                    }
                }

                callback(reviewsList)
            }

            override fun onCancelled(databaseError: DatabaseError) {
                // Handle errors
                // You might want to add some error handling here
            }
        })
    }
     fun clearUserCart() {
        val userId = getCurrentUserId()
        if (userId != null) {
            val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)
            // Remove all items from the cart
            cartReference.removeValue()
        }
    }
}
