package com.example.byteduo

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.adapter.CartAdapter
import com.example.byteduo.adapter.MenuItemsAdapter
import com.example.byteduo.model.CartItem
import com.example.byteduo.model.FirebaseDBManager.getCurrentUserId
import com.example.byteduo.model.MenuItems
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

private lateinit var cartRecyclerView: RecyclerView
private lateinit var cartAdapter: CartAdapter
private lateinit var userCartReference: DatabaseReference
private lateinit var message: TextView
class CartFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view =  inflater.inflate(R.layout.fragment_cart, container, false)

        cartRecyclerView = view.findViewById(R.id.cartRecyclerView)
        //link the recycler with the adapter
        cartRecyclerView.layoutManager= LinearLayoutManager(requireContext())
        cartAdapter = CartAdapter()

        val userId = getCurrentUserId()
        if (userId != null){
            userCartReference = FirebaseDatabase.getInstance().getReference("Cart").child(userId)
            cartRecyclerView.adapter = cartAdapter
            fetchCartData()
        }

        message = view.findViewById(R.id.txtMessage)


        return view
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
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error
            }
        })
    }


}