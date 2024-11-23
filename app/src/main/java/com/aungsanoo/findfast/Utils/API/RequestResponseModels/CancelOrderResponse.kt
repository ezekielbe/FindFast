package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class CancelOrderResponse(
    val status: Boolean,
    val message: String,
    val orderMessage: String?
)
