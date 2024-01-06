package com.example.byteduo.View

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.ClearCartCallback
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.FirebaseDBManager
import com.example.byteduo.R
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class CartFragment : Fragment(), ClearCartCallback {

    private lateinit var cartRecyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private lateinit var userCartReference: DatabaseReference
    private lateinit var message: TextView
    private lateinit var subTotal: TextView
    private lateinit var total: TextView
    private lateinit var fees: TextView
    private lateinit var btnMakePayment: Button

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_cart, container, false)

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        //link the recycler with the adapter
        cartRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter { cartItem ->

            removeItemFromCart(cartItem)

        }

        val userId = FirebaseDBManager.getCurrentUserId()
        if (userId != null){
            userCartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)
            cartRecyclerView.adapter = cartAdapter
            fetchCartData()
        }

        message = view.findViewById(R.id.txtMessage)
        subTotal= view.findViewById(R.id.txtSubtotal)
        total = view.findViewById(R.id.txtTotal)
        fees = view.findViewById(R.id.txtFee)
        btnMakePayment = view.findViewById(R.id.btnMakePayment)

        btnMakePayment.setOnClickListener {
            // Check if the cart is not empty
            isCartNotEmpty { isNotEmpty ->
                if (isNotEmpty) {
                    // Show the payment options dialog only if the cart is not empty
                    val paymentOptionsDialog = PaymentOptionsDialogFragment(this)
                    paymentOptionsDialog.show(childFragmentManager, "PaymentOptionsDialogFragment")
                } else {
                    // Show a message or take appropriate action if the cart is empty
                    Toast.makeText(
                        requireContext(),
                        "Your cart is empty. Add items before making a payment.",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
        return view
    }

    private fun removeItemFromCart(cartItem: CartItem) {
        val userId = FirebaseDBManager.getCurrentUserId()

        if (userId != null) {
            val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            // Query the cart items to find the one to remove
            val query = cartReference.orderByChild("menuItem/itemName").equalTo(cartItem.menuItem?.itemName)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (itemSnapshot in snapshot.children) {
                        // Remove the item from the cart
                        itemSnapshot.ref.removeValue()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                }
            })
        }
    }

    private fun fetchCartData() {
        // Attach a ValueEventListener to get real-time updates
        userCartReference.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val cartItems = mutableListOf<CartItem>()

                for (itemSnapshot in snapshot.children) {
                    val cartItem = itemSnapshot.getValue(CartItem::class.java)
                    if (cartItem != null) {
                        // Check if the item already exists in the cart based on itemName
                        var existingItem = cartItems.find { it.menuItem?.itemName == cartItem.menuItem?.itemName }

                        if (existingItem != null) {
                            // If the item exists, update the quantity
                            existingItem.quantity = existingItem.quantity?.plus(cartItem.quantity!!)
                        } else {
                            // If the item doesn't exist, add it to the list
                            cartItems.add(cartItem)
                        }
                    }
                }

                // Update the adapter with the fetched cart items
                cartAdapter.submitList(cartItems)

                // Update the message TextView with the number of items
                val numberOfItemsInBasket = cartItems.size
                message.text = "You have $numberOfItemsInBasket item(s) in your basket"

                // Update the subtotal
                updateTotals(cartItems)


            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }

    // Method to update total values based on cart items
    private fun updateTotals(cartItems: List<CartItem>) {

        // Calculate subtotal
        val num = cartItems.sumByDouble { (it.menuItem?.itemPrice ?: 0.0) * it.quantity!! }

        // Format the subtotal with £ symbol
        val formattedSubTotal = String.format("Subtotal: £%.2f", num)

        // Set the formatted subtotal to the TextView
        subTotal.text = formattedSubTotal

        total.text = String.format("Total: £%.2f", num)
    }

    // Override the onCartCleared method from ClearCartCallback interface
     override fun onCartCleared() {
        val userId = FirebaseDBManager.getCurrentUserId()

        if (userId != null) {
            FirebaseDBManager.clearUserCart(userId)
        }
    }

    // Method to check if the cart is not empty
    fun isCartNotEmpty(callback: (Boolean) -> Unit) {
        val userId = FirebaseDBManager.getCurrentUserId()

        if (userId != null) {
            val cartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)

            cartReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    // Invoke the callback with the result
                    callback(snapshot.exists() && snapshot.childrenCount > 0)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle error
                    callback(false) // Return false in case of an error
                }
            })
        } else {
            // Handle the case where userId is null
            callback(false)
        }
    }



    // Companion object containing a total calculation method
    companion object {
        fun total(cartItems: List<CartItem>): Double? {
            val num = cartItems.sumByDouble { (it.menuItem?.itemPrice ?: 0.0) * it.quantity!! }
            return num
        }
    }


}