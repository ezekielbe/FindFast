package com.aungsanoo.findfast

import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aungsanoo.findfast.Activities.DashboardActivity
import com.aungsanoo.findfast.Activities.LoginActivity
import com.aungsanoo.findfast.Utils.Utils

/**
 * Activity to navigate to Login or Register or Dashboard based on User session
 */
class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val isLoggedIn = handleUserSessionCheck()
        if(!isLoggedIn)
            handleNavigateLogin()
    }

    fun handleUserSessionCheck() : Boolean {
        if(Utils.validUserSession(this@MainActivity)) {
            val intent = Intent(this@MainActivity, DashboardActivity::class.java)
            startActivity(intent)
            finish()
            return true;
        }
        return false;
    }

    fun handleNavigateLogin() {
        val intent = Intent(this@MainActivity, LoginActivity::class.java)
        startActivity(intent)
        finish()
    }
}