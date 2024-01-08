package com.example.byteduo.Model


data class CartItem(
    val userId: String? = null,
    val menuItem: MenuItems?=null,
    var quantity: Int?= null,
    val total: Double? = null
){
//    fun getCartItemMenuItemId(): String? {
//        return menuItem?.getItemId()
//    }
}