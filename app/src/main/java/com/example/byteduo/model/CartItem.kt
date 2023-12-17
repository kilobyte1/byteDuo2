package com.example.byteduo.model


data class CartItem(
    val userId: String? = null,
    val menuItem: MenuItems?=null,
    var quantity: Int?= null,
    val total: Double? = null
)