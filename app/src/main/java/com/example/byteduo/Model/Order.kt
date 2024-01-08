package com.example.byteduo.Model

data class Order(
    val orderId: String? = null,
    val cusId: String? = null,
    val orderTime: String? = null,
    val orderStatus: String? = null,
    val orderItems: List<CartItem>? = null,
    val paymentType: String? = null
) {
//    fun getOrderId(): String? {
//        return this.orderId
//    }

    fun getCartItems(): List<CartItem>? {
        return this.orderItems
    }
}
