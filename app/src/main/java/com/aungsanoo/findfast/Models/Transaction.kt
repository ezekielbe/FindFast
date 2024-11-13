package com.aungsanoo.findfast.Models

data class Transaction(
    val checkout_date: String,
    val products: List<TnxProduct>,
    val total: Double,
    val user_id: String
)

data class TnxProduct(
    val id: String,
    val name: String,
    val quantity: Int
)
