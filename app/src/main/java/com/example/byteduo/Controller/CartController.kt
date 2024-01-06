package com.example.byteduo.Controller

import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.Model.MenuItems
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase

class CartController : AppCompatActivity() {

    private lateinit var databaseReference: DatabaseReference

    init {
        // Initialize the database reference in the constructor or init block
        val userId = FirebaseDBManager.getCurrentUserId()
        databaseReference = userId?.let {
            FirebaseDatabase.getInstance().getReference("Cart").child(
                it
            )
        }!!
    }

    fun handleAddToCart(menuItem: MenuItems, quantity: Int) {
        if (quantity > 0) {
            val cartItem = CartItem(
                menuItem = menuItem,
                quantity = quantity,
                total = menuItem.itemPrice?.times(quantity) ?: 0.0
            )

            // Store the cartItem in the database
            storeCartItemInDatabase(cartItem)
            Log.d("AddingToCart", "created")
        } else {
            // Handle the case where quantity is zero
        }
    }

    private fun storeCartItemInDatabase(cartItem: CartItem) {
        // Push the cartItem to the "Cart" node in the database
        databaseReference.push().setValue(cartItem)
    }
}
