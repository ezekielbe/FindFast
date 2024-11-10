package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class CartUpdateRequest(
    val user_id: String,
    val product_id: String,
    val quantity: Int
)

