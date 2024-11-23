package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderManagementDetailsClickListener
import com.aungsanoo.findfast.Adapters.OrderManagementAdapter
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminOrderManagementBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderManagementFragment: Fragment(), OnOrderManagementDetailsClickListener {
    private lateinit var binding: FragmentAdminOrderManagementBinding
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
        binding = FragmentAdminOrderManagementBinding.inflate(inflater, container, false)

        userID = Utils.getUserID(requireContext())

        if(userID != null) {
            filterButtonsUIDefault()

            handleFilterButtons()
            handleRefreshButton()

            fetchAllOrders()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    fun fetchAllOrders() {
        if (userID != null) {
            ApiClient.apiService.transactions().enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactionList = response.body()
                        if (transactionList?.isNotEmpty() == true) {
                            showOrders(true)
                            binding.orderRecyclerView.adapter = OrderManagementAdapter(transactionList, requireContext(), this@AdminOrderManagementFragment)
                        } else {
                            showOrders(false)
                            Toast.makeText(requireContext(), "There is no order to manage", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showOrders(false)
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    showOrders(false)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun showOrders(show: Boolean) {
        if(show) {
            binding.viewNoOrder.visibility = View.GONE
            binding.viewOrders.visibility = View.VISIBLE
        } else {
            binding.viewNoOrder.visibility = View.VISIBLE
            binding.viewOrders.visibility = View.GONE
        }
    }

    fun handleFilterButtons() {
        binding.filterAll.setOnClickListener{
            filterButtonsUIReset()
            binding.filterAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        }

        binding.filterInProgress.setOnClickListener{
            filterButtonsUIReset()
            binding.filterInProgress.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        }

        binding.filterDelivered.setOnClickListener{
            filterButtonsUIReset()
            binding.filterDelivered.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        }
    }

    fun handleRefreshButton() {
        binding.btnRefresh.setOnClickListener{
            fetchAllOrders()
        }
    }

    fun filterButtonsUIReset() {
        binding.filterAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
        binding.filterInProgress.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
        binding.filterDelivered.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
    }

    fun filterButtonsUIDefault() {
        binding.filterAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.filterInProgress.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
        binding.filterDelivered.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.dark_grey))
    }

    fun initRecyclerView() {
        binding.orderRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    override fun onOrderManagementDetailClick(transaction: Transaction) {
        Toast.makeText(requireContext(), "Order Details", Toast.LENGTH_SHORT).show()
    }
}