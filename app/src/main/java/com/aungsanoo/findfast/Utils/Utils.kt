package com.aungsanoo.findfast.Utils

import android.content.Context
import android.util.Log
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE

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
}