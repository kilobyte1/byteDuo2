package com.example.byteduo.model

    data class OrderDetails(
        val orderId: String,
        val cartItems: List<CartItem>
    )