package com.aungsanoo.findfast.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderManagementDetailsClickListener
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.AdminOrderItemBinding

class AdminOrderManagementAdapter(private val orderList: List<Transaction>, private val context: Context, private val listener: OnOrderManagementDetailsClickListener) : RecyclerView.Adapter<AdminOrderManagementAdapter.OrderViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminOrderManagementAdapter.OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        holder.bind(orderList[position])
    }


    override fun getItemCount(): Int {
        return orderList.size
    }

    inner class OrderViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = AdminOrderItemBinding.bind(itemView)

        fun bind(transaction: Transaction) {
            val products = transaction.products
            val status = transaction.status
            val orderDate = Utils.extractDateTime(transaction.checkout_date)?.first
            val lastUpdateDate = Utils.extractDateTime(transaction.updatedTime)?.first
            val moreThanOneProduct = products.size > 1
            val title = products.first().name
            binding.tvTitle.text = if (moreThanOneProduct) "$title,..." else title
            binding.tvOrderDate.text = "Order Date: ${orderDate}"
            binding.tvUpdateDate.text = "Last Update: ${lastUpdateDate}"
            binding.viewOrderStatus.setCardBackgroundColor(Utils.getOrderColor(status, context))
            binding.tvOrderStatus.text = Utils.getOrderStatus(status)

            binding.btnOrderDetails.setOnClickListener{
                listener.onOrderManagementDetailClick(transaction)
            }
        }
    }


}