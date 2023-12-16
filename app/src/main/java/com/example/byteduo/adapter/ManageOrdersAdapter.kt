package com.example.byteduo.adapter

import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.ToggleButton
import androidx.recyclerview.widget.RecyclerView
import com.example.byteduo.Controller.OrderHandler
import com.example.byteduo.R
import com.example.byteduo.model.Order

class ManageOrdersAdapter(private val ordersList: List<Order>) :
    RecyclerView.Adapter<ManageOrdersAdapter.OrderViewHolder>() {

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val orderNumberTextView: TextView = itemView.findViewById(R.id.orderItemorderNumberTextView)
        val orderDateTextView: TextView = itemView.findViewById(R.id.orderItemorderDateTextView)
        val itemNameAndQtyTextView: TextView = itemView.findViewById(R.id.orderItemitemNameAndQtyTextView)
        val toggleCollectPreparing: ToggleButton = itemView.findViewById(R.id.toggleCollectPreparing)
        val orderPaymentType: TextView = itemView.findViewById(R.id.orderPaymentType)
        val completeOrderBtn: TextView = itemView.findViewById(R.id.completeButton)



    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.manage_order_item_layout, parent, false)
        return OrderViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val currentOrder = ordersList[position]

        // Bind data to views in the item layout
        holder.orderNumberTextView.text = "Order #${currentOrder.orderId}"
        holder.orderDateTextView.text = " Date: ${currentOrder.orderTime}"
        holder.orderPaymentType.text ="Payment Type: ${currentOrder.paymentType}"


        val itemsNameAndQtyString = currentOrder.orderItems?.joinToString("\n") { cartItem ->
            "Item: ${cartItem.menuItem?.itemName} - Qty: ${cartItem.quantity}"
        }
        // Bind data to TextViews
        holder.itemNameAndQtyTextView.text = itemsNameAndQtyString

        holder.completeOrderBtn.setOnClickListener(){

        }

        holder.toggleCollectPreparing.setOnCheckedChangeListener { _, isChecked ->
            val newStatus = if (isChecked) "Collect" else "Preparing"

            // Update the order status based on the toggle state
            currentOrder.orderId?.let {
                OrderHandler.updateOrderStatus(it, newStatus) {
                    // This callback is invoked when the status is successfully updated
                    // Might consider sending notification
                    Log.d("OrdersAdapter", "Order status updated successfully")
                }
            }
        }


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
        return ordersList.size
    }
}
