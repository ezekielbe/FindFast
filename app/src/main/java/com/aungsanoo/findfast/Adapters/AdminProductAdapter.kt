package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.FragmentActivity
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Fragments.AdminProductDetailFragment
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.ProductBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class AdminProductAdapter(
    private val productList: MutableList<Product>,
    private val activity: FragmentActivity
) : RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    inner class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductBinding.bind(itemView)
        fun bind(product: Product) {
            // Load product image
            if (product.imageUrl?.isNotEmpty() == true) {
                Glide.with(binding.productImage.context)
                    .load(product.imageUrl)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .placeholder(R.drawable.nopic)
                    .error(R.drawable.nopic)
                    .into(binding.productImage)
            } else {
                binding.productImage.setImageResource(R.drawable.nopic)
            }

            // Set product details
            binding.productNameTxt.text = product.name
            binding.productPriceTxt.text = "Price: $${product.price}"
            binding.productDescription.text = product.description

            // Navigate to detail fragment on click
            itemView.setOnClickListener {
                val fragment = AdminProductDetailFragment.newInstance(
                    productId = product.id,
                    productName = product.name,
                    basePrice = product.basePrice,
                    productPrice = product.price,
                    productDescription = product.description,
                    productMaterial = product.material.joinToString(", "),
                    productColor = product.color.joinToString(", "),
                    productSize = product.size.joinToString(", "),
                    productAvailability = if (product.availability) "Available" else "Out of Stock",
                    qty = product.qty
                )

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AdminProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false)
        return AdminProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdminProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int = productList.size

    // Update data dynamically
    fun updateData(newProductList: List<Product>) {
        productList.clear()
        productList.addAll(newProductList)
        notifyDataSetChanged()
    }

    // Clear all data
    fun clearData() {
        productList.clear()
        notifyDataSetChanged()
    }
}
