package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.TnxProduct
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.AdminOrderProductItemBinding
import com.aungsanoo.findfast.databinding.TransactionProductItemBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

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
            val aisle: String = "Aisle: ${product.asile}"
            val bin: String = "Bin: ${product.bin}"
            val shelf: String = "Shelf: ${product.shelf}"

            if (product.imageUrl?.isNotEmpty() == true) {
                Glide.with(binding.productImage.context)
                    .load(product.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.nopic)
                    .error(R.drawable.nopic)
                    .into(binding.productImage)
            } else {
                binding.productImage.setImageResource(R.drawable.ic_product)
            }

            binding.tvName.text = name
            binding.tvCount.text = " x ${itemCount}"
            binding.tvAisle.text = aisle
            binding.tvBin.text = bin
            binding.tvShelf.text = shelf
        }
    }


}