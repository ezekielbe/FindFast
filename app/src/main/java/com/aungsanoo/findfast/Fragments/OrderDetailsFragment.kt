package com.aungsanoo.findfast.Fragments

import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.TransactionProductAdapter
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.databinding.FragmentOrderDetailsBinding

class OrderDetailsFragment: Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()

        handleBackButton()

        populateData()
    }

    fun initRecyclerView() {
        binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun handleBackButton() {
        binding.btnBack.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }

    fun populateData() {
        val transaction: Transaction? = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("transaction", Transaction::class.java)
        } else {
            arguments?.getParcelable("transaction")
        }

        transaction?.let {
            binding.tvDate.text = transaction.checkout_date
            binding.tvTotal.text = "$ ${transaction.total}"
            binding.tvCount.text = (transaction.products.size).toString()

            var products: List<TnxProduct> = transaction.products
            binding.orderItemsRecyclerView.adapter = TransactionProductAdapter(products)
        }
    }
}