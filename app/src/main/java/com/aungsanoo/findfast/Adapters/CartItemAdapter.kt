package com.aungsanoo.findfast.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.CartItem
import com.aungsanoo.findfast.databinding.CartItemBinding

// CartItemAdapter
class CartItemAdapter(private val cartItems: List<CartItem>) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.productName.text = cartItem.productName
            binding.productQuantity.text = "Quantity: ${cartItem.quantity}"
            binding.productPrice.text = "$${String.format("%.2f", cartItem.productPrice ?: 0.0)}"
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CartItemViewHolder {
        val binding = CartItemBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CartItemViewHolder(binding)
    }

    override fun onBindViewHolder(holder: CartItemViewHolder, position: Int) {
        holder.bind(cartItems[position])
    }

    override fun getItemCount(): Int = cartItems.size
}
