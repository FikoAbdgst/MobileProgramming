package com.example.uasecom.data.model

data class CartItem(
    val productId: Int = 0,
    val productName: String = "",
    val productPrice: Double = 0.0,
    val productImage: String = "",
    val quantity: Int = 1,
    val totalPrice: Double = 0.0
)