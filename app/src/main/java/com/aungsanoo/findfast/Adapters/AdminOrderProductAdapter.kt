package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.AdminOrderProductItemBinding
import com.aungsanoo.findfast.databinding.TransactionProductItemBinding

class AdminOrderProductAdapter(private val productList: List<TnxProduct>) : RecyclerView.Adapter<AdminOrderProductAdapter.AdminOrderProductViewHolder>() {
    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): AdminOrderProductAdapter.AdminOrderProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.admin_order_product_item, parent, false)
        return AdminOrderProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminOrderProductAdapter.AdminOrderProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }

    inner class AdminOrderProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = AdminOrderProductItemBinding.bind(itemView)

        fun bind(product: TnxProduct) {
            val name: String = product.name
            val itemCount: Int = product.quantity
            val price: Double = product.price
            val total: Double = itemCount * price
            binding.tvName.text = name
            binding.tvCount.text = " x ${itemCount}"
            binding.tvAisle.text = "Aisle: C3"
            binding.tvBin.text = "Bin: 9"
            binding.tvShelf.text = "Shelf: 1"
        }
    }


}