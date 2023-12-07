package com.example.byteduo.model

data class Product( val prodName: String, val prodPrice: Double,
    val prodImage: String, val prodAvailable: Boolean){

    fun addProduct(product: Product) {
        // Database insert logic here

        // Return the generated prodId

    }

    fun removeProduct() {
        // Database delete logic here

    }
}


