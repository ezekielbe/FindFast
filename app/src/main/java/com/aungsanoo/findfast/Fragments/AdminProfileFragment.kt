package com.aungsanoo.findfast.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Activities.DashboardActivity
import com.aungsanoo.findfast.Activities.LoginActivity
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminProfileBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProfileFragment: Fragment() {
    private lateinit var binding: FragmentAdminProfileBinding
    private var userID: String? = null
    private var userData: UserResponse? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminProfileBinding.inflate(inflater, container, false)

        userID = Utils.getUserID(requireContext())

        if(userID != null) {
            fetchUserData()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        parentFragmentManager.setFragmentResultListener("profileUpdated", viewLifecycleOwner) { _, _ ->
            fetchUserData()
        }

        updateTitle()

        handleEditProfileButton()

        handleOrderHistoryButton()

        handleCartButton()

        handleLogoutButton()
    }

    fun fetchUserData() {
        userID?.let {
            ApiClient.apiService.getUser(it).enqueue(object : Callback<UserResponse> {
                override fun onResponse(call: Call<UserResponse>, response: Response<UserResponse>) {
                    if (response.isSuccessful) {
                        val userResponse = response.body()
                        if(userResponse?.status == true) {
                            userData = userResponse
                            updateTitle()
                        } else {
                            Toast.makeText(requireContext(), "User not found", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Failed: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }
    }

    fun updateTitle() {
        if(userData != null) {
            binding.tvName.text = userData?.username?.replaceFirstChar { it.uppercase() } ?: "Welcome!"
            binding.tvPrivilege.text = if(userData?.isAdmin == true) "Admin" else "User"
        }
    }

    fun handleEditProfileButton() {
        binding.btnEditProfile.setOnClickListener{
            val editProfileFragment = EditprofileFragment()

            val bundle = Bundle()
            bundle.putString("id", userData?._id)
            bundle.putString("username", userData?.username)
            bundle.putString("password", userData?.password)
            bundle.putString("phone", userData?.phone)
            bundle.putString("email", userData?.email)
            bundle.putString("role", userData?.role)
            bundle.putBoolean("isAdmin", userData!!.isAdmin)
            bundle.putString("createdTime", userData?.createdTime)
            bundle.putString("updatedTime", userData?.updatedTime)

            editProfileFragment.arguments = bundle
            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, editProfileFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun handleOrderHistoryButton() {
        binding.btnOrderHistory.setOnClickListener{
            val orderHistoryFragment = OrderHistoryFragment()

            val bundle = Bundle()
            bundle.putString("id", userData?._id)
            orderHistoryFragment.arguments = bundle

            parentFragmentManager.beginTransaction()
                .replace(R.id.fragmentContainer, orderHistoryFragment)
                .addToBackStack(null)
                .commit()
        }
    }

    fun handleCartButton() {
//        binding.btnCart.setOnClickListener{
//            (activity as? DashboardActivity)?.setMenu(R.id.cartFragment)
//        }
    }

    fun handleLogoutButton() {
        binding.btnLogout.setOnClickListener{
            Utils.logoutUserSession(requireContext())
            requireActivity().finish()
            val intent = Intent(requireContext(), LoginActivity::class.java)
            startActivity(intent)
        }
    }
}