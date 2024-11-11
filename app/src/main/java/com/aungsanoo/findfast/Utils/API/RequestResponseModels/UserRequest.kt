package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class UserRequest(
    val username: String,
    val password: String,
    val phone: String,
    val email: String
)
