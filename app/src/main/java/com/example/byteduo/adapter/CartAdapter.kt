package com.example.byteduo.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.byteduo.R
import com.example.byteduo.model.CartItem

//order adapter should extend recucler
class CartAdapter : RecyclerView.Adapter<CartAdapter.ViewHolder>() {

    private var cartItems: List<CartItem> = mutableListOf()


    fun submitList(newList: List<CartItem>) {
        cartItems = newList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        // Return the total number of items in your dataset
        return cartItems.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create and return a new ViewHolder instance
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_cart_adapter, parent, false)
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
        loadRoundedImage(currentItem.menuItem?.itemImage, holder.cartItemImage, holder.itemView.context)

        holder.cartItemQty.text = currentItem.quantity.toString()

        holder.btnRemove.setOnClickListener {
            // Handle button click // remove item from cart
        }
    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

         val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
         val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
         val cartItemImage: ImageView =  itemView.findViewById(R.id.cartItemImage)
         val cartItemQty: TextView = itemView.findViewById(R.id.cartItemQty)

        //btn add
         val btnRemove: Button = itemView.findViewById(R.id.cartItemBtn)

    }

    private fun loadRoundedImage(itemImageUrl: String?, itemImageView: ImageView, context: Context) {
        // Check if itemImageUrl is not null before loading the image
        if (!itemImageUrl.isNullOrBlank()) {
            Glide.with(context)
                .load(itemImageUrl)
                .circleCrop()
                .into(itemImageView)
        } else {
            Glide.with(context)
                .load(R.drawable.aroma)
                .circleCrop()
                .into(itemImageView)
        }
    }
}
