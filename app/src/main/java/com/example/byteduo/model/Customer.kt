package com.example.byteduo.model

data class Customer(
    val fullName: String? = null,
    val email: String? = null,
    val mobile: String? = null,
    val username: String? = null,
    val role: String?="customer",
    val isActive: Boolean = false

    //not storing password as this is not safe// firebase handles the hashing



){
    // Ensure you have a default constructor
    constructor() : this("", /* other default values */)
}