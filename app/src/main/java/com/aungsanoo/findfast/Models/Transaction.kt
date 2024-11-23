package com.aungsanoo.findfast.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val checkout_date: String,
    val updatedTime: String,
    val products: List<TnxProduct>,
    val total: Double,
    val user_id: String,
    val _id: String,
    var status: Int,
    val orderMessage: String
) : Parcelable

@Parcelize
data class TnxProduct(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double,
    val asile: String,
    val bin: String,
    val imageUrl: String,
    val description: String,
    val qty: Int,
    val shelf: String
): Parcelable



//val product = {
//    "aisle": "D3",
//    "availability": true,
//    "bin": "6",
//    "color": [
//    "Gray"
//    ],
//    "description": "Rack with multiple shelves for organizing items.",
//    "id": "6730701970014d5a07d76ac3",
//    "imageUrl": "",
//    "material": [
//    "Steel"
//    ],
//    "name": "Storage Rack",
//    "price": 200.0,
//    "qty": 15,
//    "shelf": "1",
//    "size": [
//    "Large"
//    ],
//    "type": "Rack"
//}