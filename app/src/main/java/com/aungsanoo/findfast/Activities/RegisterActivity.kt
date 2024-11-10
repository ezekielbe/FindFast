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
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import com.aungsanoo.findfast.databinding.ActivityLoginBinding
import com.aungsanoo.findfast.databinding.ActivityRegisterBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class RegisterActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRegisterBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        binding = ActivityRegisterBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        handleRegisterPressed()

        handleLogin()
    }

    private fun handleRegisterPressed() {
        binding.btnRegister.setOnClickListener {
            var error: String = validate()

            if (error.isEmpty()) {
                binding.tvError.visibility = View.INVISIBLE
                registerUser()
            } else {
                renderError(error)
            }
        }
    }

    private fun validate(): String {
        //  TODO : update to regex validation e.g. email
        var name: String = binding.edtName.text.toString()
        var email: String = binding.edtEmail.text.toString()
        var phone: String = binding.edtPhone.text.toString()
        var pw1: String = binding.edtPassword.text.toString()
        var pw2: String = binding.edtPassword2.text.toString()

        var error: String = ""

        if (name.isEmpty()) error = "Name is required!"
        else if (email.isEmpty()) error = "Email is required!"
        else if (phone.isEmpty()) error = "Phone number is required!"
        else if (pw1.isEmpty()) error = "Password is required!"
        else if (pw2.isEmpty()) error = "Password is required!"
        else if (!pw2.equals(pw1)) error = "Passwords do not match!"

        return error;
    }

    private fun registerUser() {
        var username: String = binding.edtName.text.toString()
        var password: String = binding.edtPassword.text.toString()
        var phone: String = binding.edtPhone.text.toString()
        var email: String = binding.edtEmail.text.toString()
        val registerRequest = RegisterRequest(username, password, phone, "user", email, false, null)

        ApiClient.apiService.register(registerRequest).enqueue(object : Callback<RegisterResponse> {
            override fun onResponse(call: Call<RegisterResponse>, response: Response<RegisterResponse>) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    if (registerResponse?.status == true) {
                        val userId = registerResponse.userId
                        if (userId != null) {
                            val sharedPreferences = getSharedPreferences("app_prefs", MODE_PRIVATE)
                            sharedPreferences.edit().putString("user_id", userId).apply()
                            Toast.makeText(this@RegisterActivity, "Registration successful!", Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@RegisterActivity, DashboardActivity::class.java)
                            intent.putExtra("isAdmin", registerResponse.isAdmin)
                            startActivity(intent)
                        }
                    } else {
                        Toast.makeText(this@RegisterActivity, "Registration failed: ${registerResponse?.message}", Toast.LENGTH_SHORT).show()
                        registerResponse?.message?.let { renderError(it) }
                    }
                } else {
                    Toast.makeText(this@RegisterActivity, "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    renderError("Server error: ${response.code()}")
                }
            }

            override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                Toast.makeText(this@RegisterActivity, "Connection error: ${t.message}", Toast.LENGTH_SHORT).show()
                renderError("Connection error: ${t.message}")
            }
        })
    }

    private fun renderError(message: String) {
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    private fun handleLogin() {
        binding.register.setOnClickListener {
            finish()
        }
    }
}