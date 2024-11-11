package com.aungsanoo.findfast.Models

data class Product(
    val id: String,
    val name: String,
    val price: Double,
    val description: String,
    val material: Array<String>,
    val color: Array<String>,
    val size: Array<String>,
    val availability: Boolean,
    val qty: Int,
    val aisle: String,
    val type: String,
    val shelf: String,
    val bin: String
)
