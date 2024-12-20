package com.aungsanoo.findfast.Fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.TransactionProductAdapter
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CancelOrderRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CancelOrderResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentOrderDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class OrderDetailsFragment: Fragment() {
    private lateinit var binding: FragmentOrderDetailsBinding
    private var transaction: Transaction? = null

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

        handleArguments()

        handleCancelButtonUI()

        initRecyclerView()

        handleBackButton()

        handleCancelButtonListener()

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

    fun handleCancelButtonUI() {
        if(transaction != null) {
            val status: Int = transaction!!.status
            if(status in 0..1 ) {
                binding.btnCancel.visibility = View.VISIBLE
            } else {
                binding.btnCancel.visibility = View.GONE
            }
        }
    }

    fun handleCancelButtonListener() {
        binding.btnCancel.setOnClickListener{
            showActionDialog()
        }
    }

    fun showActionDialog() {
        val editText = EditText(requireContext())
        editText.hint = "Enter Cancel Message"

        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Cancel Order?")
            .setMessage("Please provide cancellation reason below.")
            .setView(editText)
            .setPositiveButton("Confirm") { dialog, _ ->
                val inputText = editText.text.toString()

                if (inputText.isNotEmpty()) {
                    apiCancelOrder(inputText)
                    dialog.dismiss()
                } else {
                    Toast.makeText(requireContext(), "Message is empty", Toast.LENGTH_SHORT).show()
                    showActionDialog()
                }
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }
            .create()

        dialog.show()
    }

    fun apiCancelOrder(message: String) {
        val messageObj: CancelOrderRequest = CancelOrderRequest(orderMessage = message)
        ApiClient.apiService.cancelOrder(transaction!!._id, messageObj).enqueue(object :
            Callback<CancelOrderResponse> {
            override fun onResponse(call: Call<CancelOrderResponse>, response: Response<CancelOrderResponse>) {
                if (response.isSuccessful) {
                    val cancelOrderResponse = response.body()
                    if (cancelOrderResponse != null) {
                        if(cancelOrderResponse.status) {
                            binding.tvOrderStatus.text = Utils.getOrderStatus(4)
                            binding.tvOrderStatus.setTextColor(Utils.getOrderColor(4, requireContext()))
                            binding.tvCancelMessage.visibility = View.VISIBLE
                            binding.tvCancelMessage.text = cancelOrderResponse.orderMessage
                            transaction!!.status = 4
                            binding.btnCancel.visibility = View.GONE
                            Toast.makeText(requireContext(), "Order cancelled successfully", Toast.LENGTH_SHORT).show()
                        } else {
                            Toast.makeText(requireContext(), "Issue with cancelling the order", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(requireContext(), "Issue with cancelling the order", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<CancelOrderResponse>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    fun handleArguments() {
        transaction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("transaction", Transaction::class.java)
        } else {
            arguments?.getParcelable("transaction")
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
            binding.tvOrderStatus.text = Utils.getOrderStatus(transaction.status)
            binding.tvOrderStatus.setTextColor(Utils.getOrderColor(transaction.status, requireContext()))

            if(transaction!!.orderMessage.isNotEmpty()) {
                binding.tvCancelMessage.visibility = View.VISIBLE
                binding.tvCancelMessage.text = transaction!!.orderMessage
            }

            var products: List<TnxProduct> = transaction.products
            binding.orderItemsRecyclerView.adapter = TransactionProductAdapter(products)
        }
    }
}