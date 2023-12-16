package com.example.byteduo.View.OrdersAdapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.R
import com.example.byteduo.model.Order

class OrdersAdapter(private val orders: List<Order>) : RecyclerView.Adapter<OrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumberTextView: TextView = itemView.findViewById(R.id.orderNumberTextView)
        val orderDateTextView: TextView = itemView.findViewById(R.id.orderDateTextView)
        val orderStatusTextView: TextView = itemView.findViewById(R.id.orderStatusTextView)
        val itemNameAndQtyTextView: TextView = itemView.findViewById(R.id.itemNameAndQtyTextView)
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

        val itemsNameAndQtyString = currentOrder.orderItems?.joinToString("\n") { cartItem ->
            "Item: ${cartItem.menuItem?.itemName} - Qty: ${cartItem.quantity}"
        }
        // Bind data to TextViews
        holder.itemNameAndQtyTextView.text = itemsNameAndQtyString



        // Fetch OrderDetails using the cusId from the database reference
//        retrieveOrderDetailsForUser(currentOrder.cusId ?: "") { orderDetailsList ->
//            // Set data for the inner RecyclerView using the setData method in OrderedItemsAdapter
//            holder.orderedItemsRecyclerView.adapter?.let {
//                if (it is OrderedItemsAdapter) {
//                    it.setData(orderDetailsList)
//                }
//            }
//        }
    }

    override fun getItemCount(): Int {
        return orders.size
    }
}
