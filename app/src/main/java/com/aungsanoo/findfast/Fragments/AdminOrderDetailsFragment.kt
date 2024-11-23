package com.aungsanoo.findfast.Fragments

import android.app.AlertDialog
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.AdminOrderProductAdapter
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CancelOrderRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CancelOrderResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminOrderDetailsBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderDetailsFragment: Fragment() {
    private lateinit var binding: FragmentAdminOrderDetailsBinding
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
        binding = FragmentAdminOrderDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleArguments()

        initRecyclerView()

        handleBackButton()

        populateData()

        optionsButtonsListener()

        handleOptionsButtonUI()
    }

    fun initRecyclerView() {
        binding.orderItemsRecyclerView.layoutManager = LinearLayoutManager(requireContext())
    }

    fun handleBackButton() {
        binding.btnBack.setOnClickListener{
            parentFragmentManager.popBackStack()
        }
    }

    fun handleArguments() {
        transaction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("transaction", Transaction::class.java)
        } else {
            arguments?.getParcelable("transaction")
        }
    }

    fun populateData() {
        transaction?.let {
            binding.tvDate.text = transaction!!.checkout_date
            binding.tvTotal.text = "$ ${transaction!!.total}"
            binding.tvCount.text = (transaction!!.products.size).toString()
            binding.tvOrderStatus.text = Utils.getOrderStatus(transaction!!.status)
            binding.tvOrderStatus.setTextColor(Utils.getOrderColor(transaction!!.status, requireContext()))

            if(transaction!!.orderMessage.isNotEmpty()) {
                binding.tvCancelMessage.visibility = View.VISIBLE
                binding.tvCancelMessage.text = transaction!!.orderMessage
            }

            var products: List<TnxProduct> = transaction!!.products
            binding.orderItemsRecyclerView.adapter = AdminOrderProductAdapter(products)
        }
    }

    fun optionsButtonsListener() {
        if(transaction != null) {
            binding.btnOptions.setOnClickListener{
                apiUpdateOrder()
            }
            binding.btnCancel.setOnClickListener{
                showActionDialog()
            }
        }
    }

    fun handleOptionsButtonUI() {
        if(transaction != null) {
            val status: Int = transaction!!.status
            if(status in 0..2 ) {
                binding.btnCancel.visibility = View.VISIBLE
                binding.btnOptions.visibility = View.VISIBLE
                binding.btnOptions.setBackgroundColor(Utils.getOrderColor(status + 1, requireContext()))
                binding.btnOptions.setText(Utils.getOrderStatusButtonText(status + 1))
            } else {
                binding.btnOptions.visibility = View.GONE
                binding.btnCancel.visibility = View.GONE
            }
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
        val messageObj: CancelOrderRequest  = CancelOrderRequest(orderMessage = message)
        ApiClient.apiService.cancelOrder(transaction!!._id, messageObj).enqueue(object : Callback<CancelOrderResponse> {
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
                            handleOptionsButtonUI()
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

    fun apiUpdateOrder() {
        ApiClient.apiService.updateOrder(transaction!!._id, transaction!!.status + 1).enqueue(object : Callback<Transaction> {
            override fun onResponse(call: Call<Transaction>, response: Response<Transaction>) {
                if (response.isSuccessful) {
                    val updatedTransaction = response.body()
                    if (updatedTransaction != null) {
                        transaction = updatedTransaction
                        populateData()
                        handleOptionsButtonUI()
                    } else {
                        Toast.makeText(requireContext(), "Issue with updating the step", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Transaction>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }
}