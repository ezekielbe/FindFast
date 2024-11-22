package com.aungsanoo.findfast.Adapters

import android.content.Context
import android.util.Log
import android.widget.Toast
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aungsanoo.findfast.Models.CartItem
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.databinding.CartItemBinding
import com.squareup.picasso.Picasso
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartItemAdapter(
    private val context: Context,
    private val cartItems: MutableList<CartItem>,
    private val onItemRemoved: (CartItem) -> Unit
) : RecyclerView.Adapter<CartItemAdapter.CartItemViewHolder>() {

    inner class CartItemViewHolder(private val binding: CartItemBinding) : RecyclerView.ViewHolder(binding.root) {
        fun bind(cartItem: CartItem) {
            binding.productName.text = cartItem.productName
            binding.productQuantity.text = "${cartItem.quantity}"
            binding.productPrice.text = "$${String.format("%.2f", cartItem.productPrice ?: 0.0)}"
            val productImageUrl = cartItem.productImageUrl
            if(!productImageUrl.isNullOrEmpty()){
                Picasso.get()
                    .load(productImageUrl)
                    .placeholder(R.drawable.logo)
                    .error(R.drawable.logo)
                    .into(binding.productImage)
            }
            binding.imageView.setOnClickListener {
                deleteCartItem(cartItem)
            }
        }

        private fun deleteCartItem(cartItem: CartItem) {
            val userId = cartItem.user_id
            val productId = cartItem.product_id

            Log.d("CartItemAdapter", "Attempting to delete item with User ID: $userId, Product ID: $productId")

            if (userId.isNullOrEmpty() || productId.isNullOrEmpty()) {
                Toast.makeText(context, "User ID or Product ID is missing", Toast.LENGTH_SHORT).show()
                return
            }

            ApiClient.apiService.deleteCartItem(userId, productId).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(context, "Item removed from cart", Toast.LENGTH_SHORT).show()
                        onItemRemoved(cartItem)
                    } else {
                        Toast.makeText(context, "Failed to remove item: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
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
