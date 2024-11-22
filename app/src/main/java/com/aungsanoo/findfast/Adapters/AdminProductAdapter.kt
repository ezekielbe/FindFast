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
import com.squareup.picasso.Picasso

class AdminProductAdapter(private val productList: List<Product>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<AdminProductAdapter.AdminProductViewHolder>() {

    inner class AdminProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductBinding.bind(itemView)
        fun bind(product: Product) {
            if(product.imageUrl?.isNotEmpty() == true){
                Picasso.get()
                    .load(product.imageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.productImage)
            }else{
                binding.productImage.setImageResource(R.drawable.logo)
            }
            binding.productImage.setImageResource(R.drawable.logo)
            binding.productNameTxt.text = product.name
            binding.productPriceTxt.text = "Price: $${product.price}"
            binding.productDescription.text = product.description


            itemView.setOnClickListener {
                val fragment = AdminProductDetailFragment.newInstance(
                    productId = product.id,
                    productName = product.name,
                    productPrice = product.price,
                    productDescription = product.description,
                    productMaterial = product.material.joinToString(", "),
                    productColor = product.color.joinToString(", "),
                    productSize = product.size.joinToString(", "),
                    productAvailability = if (product.availability) "Available" else "Out of Stock"
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

    override fun getItemCount(): Int {
        return productList.size
    }
}
