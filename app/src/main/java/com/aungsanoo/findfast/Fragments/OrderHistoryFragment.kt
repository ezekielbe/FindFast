package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderDetailsClickListener
import com.aungsanoo.findfast.Adapters.TransactionAdapter
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentOrderHistoryBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderHistoryFragment: Fragment(), OnOrderDetailsClickListener {
    private lateinit var binding: FragmentOrderHistoryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderHistoryBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        handleBackButton()

        fetchTransactions()
    }

    fun initRecyclerView() {
        binding.purchaseRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun fetchTransactions() {
        val userID = Utils.getUserID(requireContext())
        if (userID != null) {
            ApiClient.apiService.transactionsByUser(userID).enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactionList = response.body()
                        if (transactionList?.isNotEmpty() == true) {
                            showPurchases(true)
                            binding.purchaseRecyclerView.adapter = TransactionAdapter(transactionList, this@OrderHistoryFragment)
                        } else {
                            showPurchases(false)
                            Toast.makeText(requireContext(), "There is no purchase history", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showPurchases(false)
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    showPurchases(false)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun showPurchases(show: Boolean) {
        binding.viewNoPurcahse.visibility = if(show) View.GONE else View.VISIBLE
        binding.purchaseRecyclerView.visibility = if(show) View.VISIBLE else View.GONE
    }

    fun handleBackButton() {
        binding.btnBack.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }

    override fun onOrderDetailClick(transaction: Transaction) {
        val orderDetailsFragment = OrderDetailsFragment()

        val bundle = Bundle().apply {
            putParcelable("transaction", transaction)
        }

        orderDetailsFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, orderDetailsFragment)
            .addToBackStack(null)
            .commit()
    }
}