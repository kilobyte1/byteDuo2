package com.example.byteduo.model

import android.view.MenuItem

data class CartItem(
    val userId: String? = null,
    val menuItem: MenuItem?=null,
    var quantity: Int?= null,
    var subTotal: Int?= null,
    var total: Int?= null

)