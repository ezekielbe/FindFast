package com.aungsanoo.findfast.Adapters.Listeners

import com.aungsanoo.findfast.Models.Transaction

interface OnOrderDetailsClickListener {
    abstract fun onOrderDetailClick(transaction: Transaction)
}