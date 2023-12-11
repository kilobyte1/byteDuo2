package com.example.byteduo.adapter

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.model.CartItem

//order adapter shoild extend recucler
class OrderAdapter : RecyclerView.Adapter<OrderAdapter.ViewHolder>() {

    private val cartItems: List<CartItem> = mutableListOf()


    override fun getItemCount(): Int {
        // Return the total number of items in your dataset
        return cartItems.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        // Create and return a new ViewHolder instance
        val view = LayoutInflater.from(parent.context).inflate(R.layout.activity_order_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {



    }

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {

        private val cartItemName: TextView = itemView.findViewById(R.id.cartItemName)
        private val cartItemPrice: TextView = itemView.findViewById(R.id.cartItemPrice)
        private val cartItemImage: ImageView =  itemView.findViewById(R.id.cartItemImage)
        private val cartItemQty: TextView = itemView.findViewById(R.id.cartItemQty)

        //btn add
        private val btnAdd: Button = itemView.findViewById(R.id.cartItemBtn)
    }
}
