package com.aungsanoo.findfast.Utils.API.RequestResponseModels

import com.aungsanoo.findfast.Models.Transaction

data class FinancialReportResponse(
    val month: Int,
    val year: Int,
    val manufactured_cost: Double,
    val operation_cost: Double,
    val total_cost: Double,
    val total_revenue: Double,
    val total_profit: Double,
    val previous_month_revenue: Double,
    val revenue_growth: Double,
    val total_items_sold: Int,
    val total_transactions: Int,
    val transactions: List<Transaction>,
)