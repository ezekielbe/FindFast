package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.Fragments.ProductDetailFragment
import androidx.fragment.app.FragmentActivity
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.databinding.ProductBinding

class ProductAdapter(private val productList: List<Product>, private val activity: FragmentActivity) :
    RecyclerView.Adapter<ProductAdapter.ProductViewHolder>() {

    inner class ProductViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val binding = ProductBinding.bind(itemView)

        fun bind(product: Product) {
            binding.productNameTxt.text = product.name
            binding.productPriceTxt.text = "Price: $${product.price}"
            binding.productDescription.text = product.description

            itemView.setOnClickListener {
                val fragment = ProductDetailFragment.newInstance(
                    productName = product.name,
                    productPrice = product.price,
                    productDescription = product.description,
                    productMaterial = product.material.joinToString(", "),
                    productColor = product.color.joinToString(", "),
                    productSize = product.size.joinToString(", "),
                    productAvailability = product.availability
                )

                activity.supportFragmentManager.beginTransaction()
                    .replace(R.id.fragmentContainer, fragment)
                    .addToBackStack(null)
                    .commit()
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.product, parent, false)
        return ProductViewHolder(view)
    }

    override fun onBindViewHolder(holder: ProductViewHolder, position: Int) {
        holder.bind(productList[position])
    }

    override fun getItemCount(): Int {
        return productList.size
    }
}
