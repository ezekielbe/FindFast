package com.aungsanoo.findfast.Models

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Transaction(
    val checkout_date: String,
    val products: List<TnxProduct>,
    val total: Double,
    val user_id: String
) : Parcelable

@Parcelize
data class TnxProduct(
    val id: String,
    val name: String,
    val quantity: Int,
    val price: Double
): Parcelable
