package com.example.byteduo.View.OrdersAdapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.Model.Order

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumberTextView: TextView = itemView.findViewById(R.id.orderNumberTextView)
        val orderDateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
        val orderStatusTextView: TextView = itemView.findViewById(R.id.orderStatusTextView)
        val itemNameAndQtyTextView: TextView = itemView.findViewById(R.id.itemNameAndQtyTextView)
        val totalCost: TextView = itemView.findViewById(R.id.totalCost)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_order, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = orders[position]

        // Bind data to views in the item layout
        holder.orderNumberTextView.text = "Order #${currentOrder.orderId}"
        holder.orderDateTextView.text = " Date: ${currentOrder.orderTime}"
        holder.orderStatusTextView.text = " Status: ${currentOrder.orderStatus}"

        Log.d("OrdersAdapter", "Order ID: ${currentOrder.orderId}")


        //use a call back to get the total // not storing it in the database
        val total = currentOrder.orderItems?.sumByDouble { cartItem ->
            val quantity = cartItem.quantity ?: 0
            val price = cartItem.menuItem?.itemPrice ?: 0.0
            quantity * price
        } ?: 0.0

        holder.totalCost.text = "Total: £$total"

        val itemsNameAndQtyString = currentOrder.orderItems?.joinToString("\n") { cartItem ->
            "Item: ${cartItem.menuItem?.itemName} - Qty: ${cartItem.quantity} - Price: £${cartItem.total}"
        }
        // Bind data to TextViews
        holder.itemNameAndQtyTextView.text = itemsNameAndQtyString

    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
