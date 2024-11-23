package com.aungsanoo.findfast.Adapters.Listeners

import com.aungsanoo.findfast.Models.Transaction

interface OnOrderManagementDetailsClickListener {
    abstract fun onOrderManagementDetailClick(transaction: Transaction)
}