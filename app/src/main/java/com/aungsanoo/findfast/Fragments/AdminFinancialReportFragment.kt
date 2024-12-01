package com.aungsanoo.findfast.Fragments

import android.R
import android.os.Build
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Adapters.AdminOrderManagementAdapter
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.FinancialReportRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.FinancialReportResponse
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminFinancialReportBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar

class AdminFinancialReportFragment: Fragment() {
    private lateinit var binding: FragmentAdminFinancialReportBinding
    private var report: FinancialReportResponse? = null
    private var transaction: Transaction? = null

    private var selectedMonth: Int = Calendar.getInstance().get(Calendar.MONTH) + 1
    private var selectedYear: Int = Calendar.getInstance().get(Calendar.YEAR)


    val monthOptions = listOf(
        "Jan", "Feb", "Mar", "Apr", "May", "Jun",
        "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"
    )

    val yearOptions = (1900..2024).toList().reversed()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {}
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentAdminFinancialReportBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        handleArguments()

        spinnerListeners()

        populateSpinnerDefaultData()

        populateData()
    }

    fun handleArguments() {
        transaction = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            arguments?.getParcelable("transaction", Transaction::class.java)
        } else {
            arguments?.getParcelable("transaction")
        }
    }

    fun spinnerListeners() {
        binding.yearSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedYear = binding.yearSpinner.selectedItem as Int
                selectedMonth = Utils.getMonthValue(binding.monthSpinner.selectedItem as String)
                apiFetchReport(selectedYear!!, selectedMonth!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }

        binding.monthSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>, view: View?, position: Int, id: Long) {
                selectedYear = binding.yearSpinner.selectedItem as Int
                selectedMonth = Utils.getMonthValue(binding.monthSpinner.selectedItem as String)
                apiFetchReport(selectedYear!!, selectedMonth!!)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {}
        }
    }

    fun populateSpinnerDefaultData() {
        val currentYear = Calendar.getInstance().get(Calendar.YEAR)
        val yearAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, yearOptions)
        yearAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.yearSpinner.adapter = yearAdapter
        binding.yearSpinner.setSelection(yearOptions.indexOf(currentYear))

        val currentMonth = Calendar.getInstance().get(Calendar.MONTH)
        val monthAdapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, monthOptions)
        monthAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.monthSpinner.adapter = monthAdapter
        binding.monthSpinner.setSelection(currentMonth)

        apiFetchReport(currentYear, currentMonth + 1)
    }

    fun populateData() {
        binding.tvReportRange.text = "Financial report for ${monthOptions.get(selectedMonth - 1)} ${selectedYear} "

        report?.let {
            val (startDate, endDate) = Utils.getMonthDateRange(selectedYear, selectedMonth)
            binding.tvDate.text = "${startDate} - ${endDate}"
            binding.tvTotalCost.text = "$ ${String.format("%.2f", report!!.total_cost)}"
            binding.tvManufacturedCost.text = "$ ${String.format("%.2f", report!!.manufactured_cost)}"
            binding.tvOperationCost.text = "$ ${String.format("%.2f", report!!.operation_cost)}"
            binding.tvTotalRevenue.text = "$ ${String.format("%.2f", report!!.total_revenue)}"
            binding.tvTotalProfit.text = "$ ${String.format("%.2f", report!!.total_profit)}"
            binding.tvNumberOfItems.text = "${report!!.total_items_sold}"
            binding.tvNumberOfOrders.text = "${report!!.total_transactions}"
            binding.tvLastMonthRevenue.text = "$ ${String.format("%.2f", report!!.previous_month_revenue)}"
            binding.tvRevenueGrowth.text = "${String.format("%.2f", report!!.revenue_growth)}%"
        }
    }

    fun apiFetchReport(year: Int, month: Int) {
        val request = FinancialReportRequest(month, year)
        ApiClient.apiService.financialReport(request).enqueue(object : Callback<FinancialReportResponse> {
                override fun onResponse(call: Call<FinancialReportResponse>, response: Response<FinancialReportResponse>) {
                    if (response.isSuccessful) {
                        report = response.body()!!
                        populateData()
                    } else {
                        Toast.makeText(requireContext(), "Server error: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<FinancialReportResponse>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
    }
}