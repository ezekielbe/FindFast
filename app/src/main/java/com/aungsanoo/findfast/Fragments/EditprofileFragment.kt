package com.aungsanoo.findfast.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity.MODE_PRIVATE
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Activities.DashboardActivity
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentEditprofileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class EditprofileFragment: Fragment() {
    private lateinit var binding: FragmentEditprofileBinding
    private var userData: UserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentEditprofileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleArguments()

        populateData()

        handleUpdateProfileButton()

        handleCancelButton()
    }

    fun handleArguments() {
        val id = arguments?.getString("id") ?: ""
        val username = arguments?.getString("username") ?: ""
        val password = arguments?.getString("password") ?: ""
        val phone = arguments?.getString("phone") ?: ""
        val email = arguments?.getString("email") ?: ""
        val role = arguments?.getString("role") ?: ""
        val isAdmin = arguments?.getBoolean("isAdmin") ?: false
        val createdTime = arguments?.getString("createdTime") ?: ""
        val updatedTime = arguments?.getString("updatedTime") ?: ""

        userData = UserResponse(true, "", id, username, password, phone, email, role, isAdmin, createdTime, updatedTime)
    }

    fun populateData() {
        binding.edtName.setText(userData?.username)
        binding.edtEmail.setText(userData?.email)
        binding.edtPhone.setText(userData?.phone)
        binding.edtPassword.setText(userData?.password)
        binding.edtPassword2.setText(userData?.password)
        binding.tvLastUpdated.setText(userData?.updatedTime)
    }

    private fun handleUpdateProfileButton() {
        binding.btnUpdate.setOnClickListener {
            var error: String = validate()

            if (error.isEmpty()) {
                binding.tvError.visibility = View.INVISIBLE
                updateUser()
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

    private fun updateUser() {
        val username: String = binding.edtName.text.toString()
        val password: String = binding.edtPassword.text.toString()
        val phone: String = binding.edtPhone.text.toString()
        val email: String = binding.edtEmail.text.toString()
        val streetAddress: String = binding.edtStreetAddress.text.toString()
        val city: String = binding.edtCity.text.toString()
        val state: String = binding.edtState.text.toString()
        val postalCode: String = binding.edtPostalCode.text.toString()
        val country: String = binding.edtCountry.text.toString()
        val cardNumber: String = binding.edtCardNumber.text.toString()
        val cardExpiry: String = binding.edtCardExpiry.text.toString()
        val cardCvv: String = binding.edtCardCVV.text.toString()

        val userID = Utils.getUserID(requireContext())
        val userRequest = UserRequest(
            username = username,
            password = password,
            phone = phone,
            email = email,
            streetAddress = streetAddress,
            city = city,
            state = state,
            postalCode = postalCode,
            country = country,
            cardNumber = cardNumber,
            cardExpiry = cardExpiry,
            cardCvv = cardCvv
        )

        if (userID != null) {
            ApiClient.apiService.updateUser(userID, userRequest).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if (userResponse?.status == true) {
                            Toast.makeText(requireContext(), "User Profile Updated!", Toast.LENGTH_SHORT).show()
                            parentFragmentManager.setFragmentResult("profileUpdated", Bundle())
                            parentFragmentManager.popBackStack()
                        } else {
                            Toast.makeText(requireContext(), "User Profile Update failed: ${userResponse?.message}", Toast.LENGTH_SHORT).show()
                            userResponse?.message?.let { renderError(it) }
                        }
                    } else {
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                        renderError("Server error: ${response.code()}")
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Connection error: ${t.message}", Toast.LENGTH_SHORT).show()
                    renderError("Connection error: ${t.message}")
                }
            })
        } else {
            Toast.makeText(requireContext(), "Invalid User", Toast.LENGTH_SHORT).show()
            renderError("Invalid User")
        }
    }

    private fun renderError(message: String) {
        binding.tvError.visibility = View.VISIBLE
        binding.tvError.text = message
    }

    fun handleCancelButton() {
        binding.btnCancel.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }
}