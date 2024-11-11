package com.aungsanoo.findfast.Models

data class CartItem(
    val user_id: String?,
    val product_id: String?,
    val quantity: Int,
    val productName: String?,
    val productPrice: Double?
)
