package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class UserRequest(
    val username: String,
    val password: String,
    val phone: String,
    val email: String,
    val streetAddress: String?,
    val city: String?,
    val state: String?,
    val postalCode: String?,
    val country: String?,
    val cardNumber: String?,
    val cardExpiry: String?,
    val cardCvv: String?
)
