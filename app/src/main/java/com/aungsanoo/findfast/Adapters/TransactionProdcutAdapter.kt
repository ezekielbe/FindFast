package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.TransactionProductItemBinding

class TransactionProductAdapter(private val productList: List<TnxProduct>) : RecyclerView.Adapter<TransactionProductAdapter.TransactionProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TransactionProductAdapter.TransactionProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.transaction_product_item, parent, false)
        return TransactionProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: TransactionProductAdapter.TransactionProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class TransactionProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = TransactionProductItemBinding.bind(itemView)

        fun bind(product: TnxProduct) {
            val name: String = product.name
            val itemCount: Int = product.quantity
            val price: Double = product.price
            val total: Double = itemCount * price
            binding.tvName.text = name
            binding.tvCount.text = " x ${itemCount}"
            binding.tvPrice.text = "Price/item: $ ${price}"
            binding.tvTotal.text = "Total: $ ${total}"
        }
    }


}