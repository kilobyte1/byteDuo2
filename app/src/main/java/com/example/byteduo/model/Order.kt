package com.example.byteduo.model

data class Order(
    val orderId: String? = null,
    val cusId: String? = null,
    val orderTime: String? = null,
    val orderStatus: String? = null,
    val orderItems: List<CartItem>?=null,
    val paymentType: String? = null
)