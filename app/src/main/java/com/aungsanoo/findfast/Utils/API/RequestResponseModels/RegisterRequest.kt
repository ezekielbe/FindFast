package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class RegisterRequest(
    val username: String,
    val password: String,
    val phone: String,
    val role: String,
    val email: String,
    val isAdmin: Boolean,
    val defaultAddress: String?
)
