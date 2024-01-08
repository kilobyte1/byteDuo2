package com.example.byteduo.Controller

import android.util.Log
import com.example.byteduo.View.CartFragment
import com.example.byteduo.Model.CartItem
import com.example.byteduo.Model.Order
import com.example.byteduo.Model.OrderDetails
import com.example.byteduo.Model.Payment
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class OrderHandler {




    companion object {
        private lateinit var ordersDatabaseReference: DatabaseReference


        fun updateOrderStatus(orderId: String, newStatus: String, callback: () -> Unit) {
            val orderReference = FirebaseDatabase.getInstance().getReference("Orders").child(orderId)

            // Assuming "orderStatus" is the field in your Order class representing the order status
            orderReference.child("orderStatus").setValue(newStatus)
                .addOnSuccessListener {
                    // Handle success, e.g., show a success message
                    callback()
                }
                .addOnFailureListener {
                    // Handle the failure
                }
        }






        fun createOrderAndDetails(cartItems: List<CartItem>,paymentType: String,callback: (String) -> Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                val orderCounterReference = FirebaseDatabase.getInstance().getReference("OrderCounter")

                orderCounterReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val currentOrderCounter = snapshot.getValue(Long::class.java) ?: 0

                        val orderTime = SimpleDateFormat("dd-MM-yyyy HH:mm", Locale.UK).format(Date())

                        val orderId = String.format("%06d", currentOrderCounter + 1)

                        // Create an Order object
                        val order = Order(
                            orderId = orderId,
                            cusId = userId,
                            orderTime = orderTime,
                            orderStatus = "Preparing",
                            orderItems = cartItems,
                            paymentType = paymentType,
                        )

                        // Save the order to the database
                        saveOrderToDatabase(order)

                        // Create OrderDetails object
                        val allMenuItemIds: String = cartItems.mapNotNull { it.menuItem?.itemId }.joinToString(", ")

                        // Create a single OrderDetails object with the concatenated menuItemIds
                        val orderDetails = OrderDetails(
                            orderId = orderId,
                            menuItemId = allMenuItemIds
                        )

                        // Save the order details to the database
                        saveOrderDetailsToDatabase(orderDetails)

                        // Increment the order ID counter for the next order
                        orderCounterReference.setValue(currentOrderCounter + 1)

                        // Create Payment object
                        val payment = Payment(
                            OrderId = orderId,
                            PaymentType = paymentType,
                            Amount = CartFragment.total(cartItems),
                            PaymentDate = orderTime
                        )

                        // Save the payment to the database
                        savePaymentToDatabase(payment)

                        // Invoke the callback with the created order ID
                        callback(orderId)
                    }

                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                    }
                })
            }
        }

        private fun savePaymentToDatabase(payment: Payment) {
            val paymentReference =
                FirebaseDatabase.getInstance().getReference("Payments").child(payment.OrderId!!)
            paymentReference.setValue(payment)
                .addOnSuccessListener {
                    // Handle success, e.g., show a success message
                    Log.d("Payment","Payment stored successfully.")
                }
                .addOnFailureListener {
                    Log.d("Payment","Payment details not saved")
                }
        }

        fun saveOrderToDatabase(order: Order) {
            val databaseReference = FirebaseDatabase.getInstance().getReference("Orders")
            val orderReference = order.orderId?.let { databaseReference.child(it) }

            if (orderReference != null) {
                orderReference.setValue(order)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Order saved successfully
                        } else {
                            // Error occurred while saving the order
                        }
                    }
            }
        }




        private fun saveOrderDetailsToDatabase(orderDetails: OrderDetails) {
            val orderDetailsReference = orderDetails.orderId?.let {
                FirebaseDatabase.getInstance().getReference("OrderDetails")
                    .child(it)
            }

            if (orderDetailsReference != null) {
                orderDetailsReference.setValue(orderDetails)
                    .addOnCompleteListener { task ->
                        if (task.isSuccessful) {
                            // Order details saved successfully
                        } else {
                            // Error occurred while saving the order details
                        }
                    }
            }
        }



        val cartReference = FirebaseDatabase.getInstance().getReference("Cart")
        fun retrieveCartItemsFromDatabase(callback: (List<CartItem>) -> Unit) {
            val userId = FirebaseAuth.getInstance().currentUser?.uid

            if (userId != null) {
                // Assume you store cart items under a "Cart" node with the user's ID
                val userCartReference = cartReference.child(userId)

                userCartReference.addListenerForSingleValueEvent(object : ValueEventListener {
                    override fun onDataChange(snapshot: DataSnapshot) {
                        val cartItems = mutableListOf<CartItem>()

                        for (itemSnapshot in snapshot.children) {
                            val cartItem = itemSnapshot.getValue(CartItem::class.java)
                            cartItem?.let {
                                cartItems.add(it)
                            }
                        }

                        // Invoke the callback with the list of cart items
                        callback(cartItems)
                    }
                    override fun onCancelled(error: DatabaseError) {
                        // Handle the error
                    }
                })
            }

        }


        var databaseReference = FirebaseDatabase.getInstance().getReference("Orders")

        fun retrieveOrdersForUser(userId: String, callback: (List<Order>) -> Unit) {
            val userOrdersReference = databaseReference.orderByChild("cusId").equalTo(userId)

            userOrdersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()

                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        order?.let {
                            orders.add(it)
                        }
                    }

                    callback(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }

        fun retrieveAllOrders(callback: (List<Order>) -> Unit) {
            val allOrdersReference = databaseReference

            allOrdersReference.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val orders = mutableListOf<Order>()

                    for (orderSnapshot in snapshot.children) {
                        val order = orderSnapshot.getValue(Order::class.java)
                        order?.let {
                            orders.add(it)
                        }
                    }

                    callback(orders)
                }

                override fun onCancelled(error: DatabaseError) {
                    // Handle the error
                }
            })
        }


    }
}