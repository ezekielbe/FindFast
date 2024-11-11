package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class UserResponse(
    val status: Boolean,
    val message: String,
    val _id: String,
    val username: String,
    val password: String,
    val phone: String,
    val email: String,
    val role: String,
    val isAdmin: Boolean,
    val createdTime: String,
    val updatedTime: String
)
