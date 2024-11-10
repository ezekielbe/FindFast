package com.aungsanoo.findfast.Fragments

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Activities.DashboardActivity
import com.aungsanoo.findfast.Activities.LoginActivity
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentProfileBinding

class ProfileFragment: Fragment() {
    private lateinit var binding: FragmentProfileBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleLogoutButton()
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