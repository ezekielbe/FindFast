package com.aungsanoo.findfast.Utils.API.RequestResponseModels

data class ProductUpdateRequest(
    val productId: String,
    val name: String,
    val price: Double,
    val basePrice: Double,
    val description: String,
    val material: String,
    val color: String,
    val size: String,
    val availability: Boolean,
    val imageUrl: String
)
