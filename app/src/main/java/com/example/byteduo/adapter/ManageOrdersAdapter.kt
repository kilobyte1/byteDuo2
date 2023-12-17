package com.example.byteduo.adapter

import android.app.NotificationManager
import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.Spinner
import android.widget.TextView
import android.widget.ToggleButton
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat.getSystemService
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
        val orderPaymentType: TextView = itemView.findViewById(R.id.orderPaymentType)
        val orderStatusSpinner: Spinner = itemView.findViewById(R.id.threeStateSpinner)
        val totalCost: TextView = itemView.findViewById(R.id.orderTotal)


        val notificationManager = itemView.context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        private  val CHANNEL_ID = "order_update"

        // Define your notification builder
        val builder = NotificationCompat.Builder(itemView.context, CHANNEL_ID)
            .setSmallIcon(R.drawable.byteduo_coffeee)
            .setContentTitle("Notification Title") // Replace with your title
            .setContentText("Notification Content") // Replace with your content
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)




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

        // Set the selection based on the current order status
        val statusArray = holder.itemView.resources.getStringArray(R.array.order_statuses)
        val selectedPosition = statusArray.indexOf(currentOrder.orderStatus)

        if (selectedPosition != -1) {
            holder.orderStatusSpinner.setSelection(selectedPosition)
        }

        holder.orderStatusSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                val selectedStatus = parent?.getItemAtPosition(position).toString()

                // Update the order status based on the selected option
                currentOrder.orderId?.let {
                    OrderHandler.updateOrderStatus(it, selectedStatus) {
                        // This callback is invoked when the status is successfully updated
                        // ToDo
                        //Send a notification to the customer

                        // Display the notification
                        holder.notificationManager.notify(0, holder.builder.build())
                        Log.d("OrdersAdapter", "Order status updated successfully to $selectedStatus")
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handle case where nothing is selected
            }
        }

        //use a call back to get the total // not storing it in the database
        val total = currentOrder.orderItems?.sumByDouble { cartItem ->
            val quantity = cartItem.quantity ?: 0
            val price = cartItem.menuItem?.itemPrice ?: 0.0
            quantity * price
        } ?: 0.0

        holder.totalCost.text = "Total: £$total"





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
