package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.Transformation
import android.widget.LinearLayout
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderManagementDetailsClickListener
import com.aungsanoo.findfast.Adapters.AdminOrderManagementAdapter
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.TransactionsByDateRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminOrderManagementBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminOrderManagementFragment: Fragment(), OnOrderManagementDetailsClickListener {
    private lateinit var binding: FragmentAdminOrderManagementBinding
    private var userID: String? = null
    private var selectedStatus: Int = 0
    private var selectedRange: String = "all"
    private var transactions: List<Transaction> = emptyList()


    private var isExpanded = false

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

            handleFilterButtons() // status filter

            handleRangeFilterToggleButton() // range filter
            handleRangeFilterButtons()

            handleRefreshButton()

            fetchOrders()
        }

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initRecyclerView()
    }

    fun fetchOrders() {
        if (userID != null) {
            ApiClient.apiService.transactions().enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactionList = response.body()
                        if (transactionList?.isNotEmpty() == true) {
                            showOrders(true)
                            transactions = transactionList
                            updateOrderListInAdapter(Utils.getOrdersBasedOnStatus(transactions, selectedStatus))
                        } else {
                            transactions = emptyList()
                            showOrders(false)
                            Toast.makeText(requireContext(), "There is no order to manage", Toast.LENGTH_SHORT).show()
                        }
                        updateRangeAndCountText(selectedRange, Utils.getOrdersBasedOnStatus(transactions, selectedStatus).size)
                    } else {
                        transactions = emptyList()
                        showOrders(false)
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    transactions = emptyList()
                    showOrders(false)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }
    }

    fun showOrders(show: Boolean) {
        if(show) {
            binding.viewNoOrder.visibility = View.GONE
            binding.orderRecyclerView.visibility = View.VISIBLE
        } else {
            binding.viewNoOrder.visibility = View.VISIBLE
            binding.orderRecyclerView.visibility = View.GONE
        }
    }

    fun handleFilterButtons() {
        binding.filterAll.setOnClickListener{
            filterButtonsUIReset()
            binding.filterAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            updateOrderListInAdapter(transactions)
            selectedStatus = 0
            updateRangeAndCountText(selectedRange, transactions.size)
        }

        binding.filterInProgress.setOnClickListener{
            filterButtonsUIReset()
            binding.filterInProgress.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            updateOrderListInAdapter(Utils.getInProgressOrders(transactions))
            selectedStatus = 1
            updateRangeAndCountText(selectedRange, Utils.getInProgressOrders(transactions).size)
        }

        binding.filterDelivered.setOnClickListener{
            filterButtonsUIReset()
            binding.filterDelivered.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            updateOrderListInAdapter(Utils.getDeliveredOrders(transactions))
            selectedStatus = 2
            updateRangeAndCountText(selectedRange, Utils.getDeliveredOrders(transactions).size)
        }
    }

    fun updateOrderListInAdapter(transactions: List<Transaction>) {
        binding.orderRecyclerView.adapter = AdminOrderManagementAdapter(transactions, requireContext(), this@AdminOrderManagementFragment)
    }

    fun handleRangeFilterToggleButton() {
        binding.btnRangeFilter.setOnClickListener{
            if (isExpanded) {
                collapse(binding.rangeFilterView)
            } else {
                expand(binding.rangeFilterView)
            }
            isExpanded = !isExpanded
        }
    }

    fun handleRefreshButton() {
        binding.btnRefresh.setOnClickListener{
            if(selectedRange == "all") {
                fetchOrders()
            } else {
                updateDateRange(selectedRange)
            }
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
        val adminOrderDetailsFragment = AdminOrderDetailsFragment()

        val bundle = Bundle().apply {
            putParcelable("transaction", transaction)
        }

        adminOrderDetailsFragment.arguments = bundle

        parentFragmentManager.beginTransaction()
            .replace(R.id.fragmentContainer, adminOrderDetailsFragment)
            .addToBackStack(null)
            .commit()
    }

    fun expand(view: View) {
        view.measure(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
        val targetHeight = view.measuredHeight

        view.layoutParams.height = 0
        view.visibility = View.VISIBLE

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                view.layoutParams.height =
                    if (interpolatedTime == 1f) LinearLayout.LayoutParams.WRAP_CONTENT
                    else (targetHeight * interpolatedTime).toInt()
                view.requestLayout()
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration = (targetHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }

    fun collapse(view: View) {
        val initialHeight = view.measuredHeight

        val animation = object : Animation() {
            override fun applyTransformation(interpolatedTime: Float, t: Transformation?) {
                if (interpolatedTime == 1f) {
                    view.visibility = View.GONE
                } else {
                    view.layoutParams.height =
                        initialHeight - (initialHeight * interpolatedTime).toInt()
                    view.requestLayout()
                }
            }

            override fun willChangeBounds(): Boolean = true
        }

        animation.duration =
            (initialHeight / view.context.resources.displayMetrics.density).toLong()
        view.startAnimation(animation)
    }

    fun handleRangeFilterButtons() {
        binding.rangeDay.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeDay.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "1D"
            updateDateRange(selectedRange)

        }

        binding.rangeWeek.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeWeek.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "1W"
            updateDateRange(selectedRange)

        }

        binding.rangeMonth.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeMonth.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "1M"
            updateDateRange(selectedRange)
        }

        binding.rangeQuarter.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeQuarter.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeQuarter.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "3M"
            updateDateRange(selectedRange)
        }

        binding.rangeYear.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeYear.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "1Y"
            updateDateRange(selectedRange)
        }

        binding.rangeAll.setOnClickListener{
            rangeFilterButtonsUnselectDefaultUI()
            binding.rangeAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.secondary))
            binding.tvRangeAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.white))
            selectedRange = "all"
            fetchOrders()
        }
    }

    fun rangeFilterButtonsUnselectDefaultUI() {
        binding.rangeDay.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.rangeWeek.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.rangeMonth.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.rangeQuarter.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.rangeYear.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))
        binding.rangeAll.setCardBackgroundColor(ContextCompat.getColor(requireContext(), R.color.white))

        binding.tvRangeDay.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.tvRangeWeek.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.tvRangeMonth.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.tvRangeQuarter.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.tvRangeYear.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
        binding.tvRangeAll.setTextColor(ContextCompat.getColor(requireContext(), R.color.secondary))
    }

    fun updateDateRange(rangeFilter: String) {
        val (startDate, endDate) = Utils.getDateRange(rangeFilter)
        val payload = TransactionsByDateRequest(startDate, endDate)

        if (userID != null) {
            ApiClient.apiService.transactionsByDate(payload).enqueue(object : Callback<List<Transaction>> {
                override fun onResponse(call: Call<List<Transaction>>, response: Response<List<Transaction>>) {
                    if (response.isSuccessful) {
                        val transactionList = response.body()
                        if (transactionList?.isNotEmpty() == true) {
                            showOrders(true)
                            transactions = transactionList
                            updateOrderListInAdapter(Utils.getOrdersBasedOnStatus(transactions, selectedStatus))
                        } else {
                            transactions = emptyList()
                            showOrders(false)
                            Toast.makeText(requireContext(), "There is no order to manage", Toast.LENGTH_SHORT).show()
                        }
                        updateRangeAndCountText(rangeFilter, Utils.getOrdersBasedOnStatus(transactions, selectedStatus).size)
                    } else {
                        transactions = emptyList()
                        showOrders(false)
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<Transaction>>, t: Throwable) {
                    transactions = emptyList()
                    showOrders(false)
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }


    }

    fun updateRangeAndCountText(range: String, count: Int) {
        if(range == "all") {
            binding.tvOrdersRange.text = "All Orders"
        } else if(range == "1D") {
            binding.tvOrdersRange.text = "Today Orders"
        } else {
            val (startDate, endDate) = Utils.getDateRange(range)
            binding.tvOrdersRange.text = "Orders from ${startDate} to ${endDate}"
        }

        binding.tvCount.text = "Count: ${count}"
    }
}