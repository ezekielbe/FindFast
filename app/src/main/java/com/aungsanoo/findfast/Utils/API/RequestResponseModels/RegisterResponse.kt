package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class RegisterResponse(
    val status: Boolean,
    val message: String?,
    val userId: String?,
    val isAdmin: Boolean
)
