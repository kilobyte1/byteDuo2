package com.example.byteduo.Controller

import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.byteduo.Controller.OrderHandler.Companion.databaseReference
import com.example.byteduo.model.CartItem
import com.example.byteduo.model.FirebaseDBManager
import com.example.byteduo.model.MenuItems
import com.google.firebase.database.FirebaseDatabase

class CartController: AppCompatActivity() {

    private fun handleAddToCart(menuItem: MenuItems, quantity: Int) {
        if (quantity > 0) {
            // Create a CartItem object with the necessary details
            val cartItem = CartItem(
                menuItem = menuItem,
                quantity = quantity,
            )
            // Store the cartItem in the database or perform other actions as needed
            storeCartItemInDatabase(cartItem)
            Toast.makeText(this@CartController, "Item added", Toast.LENGTH_SHORT).show()

        } else {

            // Show a toast message if quantity is zero
            Toast.makeText(this@CartController, "Quantity cannot be 0", Toast.LENGTH_SHORT).show()
        }
    }

    private fun storeCartItemInDatabase(cartItem: CartItem) {

        val userId = FirebaseDBManager.getCurrentUserId()

        if (userId != null) {
            databaseReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            databaseReference.push().setValue(cartItem)

        }
    }
}