package com.example.byteduo.View

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.ImageLoaderUtil

//order adapter should extend recycler
class CartAdapter(private val removeItemListener: (CartItem) -> Unit) : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var cartItems: List<CartItem> = mutableListOf()


    fun submitList(newList: List<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        // Return the total number of items in the cart
        return cartItems.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create and return a new ViewHolder instance
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_cart_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val currentItem = cartItems[position]

        // Bind data to views
        holder.cartItemName.text = currentItem.menuItem?.itemName ?: ""
        // Format the price with £ symbol
        val formattedPrice = String.format("£%.2f", currentItem.menuItem?.itemPrice ?: 0.0)
        holder.cartItemPrice.text = formattedPrice

        // Load the rounded image using the loadRoundedImage function
        currentItem.menuItem?.itemImage?.let { ImageLoaderUtil.loadRoundedImage(it, holder.cartItemImage, holder.itemView.context) }

        holder.cartItemQty.text = String.format("Qty: %s", currentItem.quantity.toString())

        holder.btnRemove.setOnClickListener {
            // Handle button click // remove item from cart
            // Call the removeItemListener with the current item when the button is clicked
            removeItemListener.invoke(currentItem)
        }
    }

    //create a view holder for the cart
    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
         val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
         val cartItemImage: ImageView =  itemView.findViewById(R.id.cartItemImage)
         val cartItemQty: TextView = itemView.findViewById(R.id.cartItemQty)

        //btn remove
         val btnRemove: Button = itemView.findViewById(R.id.cartItemRemove)

    }

}