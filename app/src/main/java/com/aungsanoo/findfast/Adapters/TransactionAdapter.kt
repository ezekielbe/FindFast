package com.aungsanoo.findfast.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderDetailsClickListener
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.Utils
import com.aungsanoo.findfast.databinding.TransactionItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class TransactionAdapter(private val tnxList: List<Transaction>, private val context: Context, private val listener: OnOrderDetailsClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionAdapter.TransactionViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_item, parent, false)
        return TransactionViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionAdapter.TransactionViewHolder, position: Int) {
        holder.bind(tnxList[position])
    }

    override fun getItemCount(): Int {
        return tnxList.size
    }

    inner class TransactionViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TransactionItemBinding.bind(itemView)

        fun bind(transaction: Transaction) {
            val status = transaction.status
            val products = transaction.products
            val moreThanOneProduct = products.size > 1
            val title = products.first().name
            binding.tvTitle.text = if (moreThanOneProduct) "$title,..." else title
            binding.tvDate.text = "Order Date: ${Utils.extractDateTime(transaction.checkout_date)?.first}"
            binding.tvTotal.text = "Total: $ ${transaction.total}"
            binding.viewOrderStatus.setCardBackgroundColor(Utils.getOrderColor(status, context))
            binding.tvOrderStatus.text = Utils.getOrderStatus(status)

            for (product in products) {
                if (product.imageUrl?.isNotEmpty() == true) {
                    Glide.with(binding.firstProductInOrder.context)
                        .load(product.imageUrl)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(R.drawable.nopic)
                        .error(R.drawable.nopic)
                        .into(binding.firstProductInOrder)
                    break
                } else {
                    binding.firstProductInOrder.setImageResource(R.drawable.ic_no_purchase)
                }
            }

            binding.btnTransactionDetails.setOnClickListener{
                listener.onOrderDetailClick(transaction)
            }
        }
    }


}