package com.aungsanoo.findfast.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.databinding.ActivityLoginBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LoginActivity : AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleLoginPressed()
        handleRegister()
    }

    private fun handleLoginPressed() {
        binding.btnLogin.setOnClickListener {
            binding.tvError.visibility = View.INVISIBLE
            val error = validate()

            if (error.isEmpty()) {
                loginUser()
            } else {
                renderError(error)
            }
        }
    }

    private fun validate(): String {
        val name: String = binding.edtName.text.toString()
        val pw: String = binding.edtPassword.text.toString()

        return when {
            name.isEmpty() -> "Name is required!"
            pw.isEmpty() -> "Password is required!"
            else -> ""
        }
    }

    private fun loginUser() {
        val username: String = binding.edtName.text.toString()
        val password: String = binding.edtPassword.text.toString()
        val loginRequest = LoginRequest(username, password)

        ApiClient.apiService.login(loginRequest).enqueue(object : Callback<LoginResponse> {
            override fun onResponse(call: Call<LoginResponse>, response: Response<LoginResponse>) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    if (loginResponse?.status == true) {
                        val userId = loginResponse.userId
                        if (userId != null) {
                            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_id", userId).apply()
                            sharedPreferences.edit().putBoolean("isAdmin", loginResponse.isAdmin).apply()

                            Toast.makeText(this@LoginActivity, "Login successful!", Toast.LENGTH_SHORT).show()


                            val intent = if (loginResponse.isAdmin == true) {
                                Intent(this@LoginActivity, AdminDashboard::class.java)
                            } else {
                                Intent(this@LoginActivity, DashboardActivity::class.java)
                            }
                            intent.putExtra("isAdmin", loginResponse.isAdmin)
                            startActivity(intent)
                            finish()
                        }
                    } else {
                        Toast.makeText(this@LoginActivity, "Login failed: ${loginResponse?.message}", Toast.LENGTH_SHORT).show()
                        loginResponse?.message?.let { renderError(it) }
                    }
                } else {
                    Toast.makeText(this@LoginActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    renderError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                Toast.makeText(this@LoginActivity, "Connection error: ${t.message}", Toast.LENGTH_SHORT).show()
                renderError("Connection error: ${t.message}")
            }
        })
    }

    private fun renderError(message: String) {
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    private fun handleRegister() {
        binding.register.setOnClickListener {
            val intent = Intent(this@LoginActivity, RegisterActivity::class.java)
            startActivity(intent)
        }
    }
}
