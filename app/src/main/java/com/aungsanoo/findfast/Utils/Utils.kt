package com.aungsanoo.findfast.Utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.core.content.ContextCompat
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import java.text.SimpleDateFormat
import java.util.Locale

object Utils {
    fun validUserSession(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        return userId != null
    }

    fun logoutUserSession(context: Context) {
        val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val editor = sharedPreferences.edit()
        editor.remove("user_id")  // Remove the key-value pair
        editor.apply()
    }

    fun getUserID(context: Context): String? {
        val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)
        return userId
    }

    fun isUserAdmin(context: Context): Boolean {
        val sharedPreferences = context.getSharedPreferences("app_prefs", MODE_PRIVATE)
        val isAdmin = sharedPreferences.getBoolean("isAdmin", false)
        return isAdmin
    }

    fun getOrderStatus(status: Int): String {
        return when (status) {
            0 -> "Order Received"
            1 -> "Ready to Deliver" // or item fetched in warehouse by admin
            2 -> "In Transit"
            3 -> "Delivered"
            4 -> "Order Cancelled"
            else -> "Unknown Status"
        }
    }

    fun getOrderStatusButtonText(status: Int): String {
        return when (status) {
            0 -> "Order Received"
            1 -> "Check items availability" // or item fetched in warehouse by admin
            2 -> "Start Delivery"
            3 -> "Delivered"
            4 -> "Order Cancelled"
            else -> "Unknown Status"
        }
    }

    fun getOrderColor(status: Int, context: Context): Int {
        return when (status) {
            0 -> ContextCompat.getColor(context, R.color.order_received)  // Yellow
            1 -> ContextCompat.getColor(context, R.color.item_fetched)   // Green
            2 -> ContextCompat.getColor(context, R.color.in_transit)      // Blue
            3 -> ContextCompat.getColor(context, R.color.delivered)       // Green
            4 -> ContextCompat.getColor(context, R.color.order_cancelled) // Red
            else -> ContextCompat.getColor(context, R.color.order_received) // Default to yellow
        }
    }

    fun extractDateTime(dateString: String): Pair<String, String>? {
        try {
            val inputFormat = SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss z", Locale.ENGLISH)

            val date = inputFormat.parse(dateString)

            val dateFormat = SimpleDateFormat("EEE, dd MMM yyyy", Locale.ENGLISH)

            val timeFormat = SimpleDateFormat("HH:mm:ss z", Locale.ENGLISH)

            val formattedDate = dateFormat.format(date)
            val formattedTime = timeFormat.format(date)

            return Pair(formattedDate, formattedTime)
        } catch (e: Exception) {
            e.printStackTrace()
            return null
        }
    }

    fun getInProgressOrders(transactionList: List<Transaction>): List<Transaction> {
        return transactionList.filter { it.status in 0..2 }
    }

    fun getDeliveredOrders(transactionList: List<Transaction>): List<Transaction> {
        return transactionList.filter { it.status == 3 }
    }

    fun getMonthValue(month: String): Int {
        val monthOptions = listOf(
            "Jan", "Feb", "Mar", "Apr", "May", "Jun",
            "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
        )
        return monthOptions.indexOf(month).takeIf { it >= 0 }?.plus(1) ?: 1
    }



}