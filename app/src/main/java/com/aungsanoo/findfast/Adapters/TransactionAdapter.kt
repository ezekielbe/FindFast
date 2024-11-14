package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Adapters.Listeners.OnOrderDetailsClickListener
import com.aungsanoo.findfast.Models.Transaction
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.TransactionItemBinding

class TransactionAdapter(private val tnxList: List<Transaction>, private val listener: OnOrderDetailsClickListener) : RecyclerView.Adapter<TransactionAdapter.TransactionViewHolder>() {
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
            val products = transaction.products
            val moreThanOneProduct = products.size > 1
            val title = products.first().name
            binding.tvTitle.text = if (moreThanOneProduct) "$title,..." else title
            binding.tvDate.text = "Purchase Date: ${transaction.checkout_date}"
            binding.tvTotal.text = "Total: $ ${transaction.total}"

            binding.btnTransactionDetails.setOnClickListener{
                listener.onOrderDetailClick(transaction)
            }
        }
    }


}