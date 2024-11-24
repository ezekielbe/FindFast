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
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.FragmentAdminFinancialReportBinding
import java.util.Calendar

class AdminFinancialReportFragment: Fragment() {
    private lateinit var binding: FragmentAdminFinancialReportBinding
    private var transaction: Transaction? = null
    private var selectedMonth: Int = 11
    private var selectedYear: Int = 2024

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

        apiFetchReport(currentYear, currentMonth)
    }

    fun populateData() {
        binding.tvReportRange.text = "Financial report for ${monthOptions.get(selectedMonth - 1)} ${selectedYear} "

        transaction?.let {
            binding.tvDate.text = transaction!!.checkout_date

//            binding.tvTotal.text = "$ ${transaction!!.total}"
//            binding.tvCount.text = (transaction!!.products.size).toString()
//            binding.tvOrderStatus.text = Utils.getOrderStatus(transaction!!.status)
//            binding.tvOrderStatus.setTextColor(Utils.getOrderColor(transaction!!.status, requireContext()))
        }
    }

    fun apiFetchReport(year: Int, month: Int) {
        Toast.makeText(requireContext(), "Year: $year, Month: ${month}", Toast.LENGTH_SHORT).show()
        populateData()
    }
}