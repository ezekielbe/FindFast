package com.aungsanoo.findfast.Fragments

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.CartItemAdapter
import com.aungsanoo.findfast.Models.CartItem
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.databinding.FragmentCartBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CartFragment : Fragment() {
    private var _binding: FragmentCartBinding? = null
    private val binding get() = _binding!!
    private lateinit var cartAdapter: CartItemAdapter
    private var cartItems: MutableList<CartItem> = mutableListOf()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentCartBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        binding.cartRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        loadCartItems()
        binding.checkoutButton.setOnClickListener {
            checkout()
        }
    }

    private fun checkout() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            ApiClient.apiService.checkout(mapOf("user_id" to userId)).enqueue(object : Callback<ResponseBody> {
                override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                    if (response.isSuccessful) {
                        Toast.makeText(requireContext(), "Checkout successful!", Toast.LENGTH_SHORT).show()

                        // Update quantity in database for each item
                        updateProductQuantities()

                        cartItems.clear()
                        cartAdapter.notifyDataSetChanged()
                        updateTotalPrice(cartItems)
                    } else {
                        Toast.makeText(requireContext(), "Checkout failed: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateProductQuantities() {
        cartItems.forEach { cartItem ->
            val productId = cartItem.product_id
            val newQuantity = cartItem.quantity - cartItem.quantity

            if (newQuantity >= 0) {
                if (productId != null) {
                    ApiClient.apiService.updateProductQuantity(productId, newQuantity).enqueue(object : Callback<ResponseBody> {
                        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                            if (response.isSuccessful) {
                                Log.d("CartFragment", "Product $productId quantity updated successfully")
                            } else {
                                Log.e("CartFragment", "Failed to update product $productId quantity: ${response.code()}")
                            }
                        }

                        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                            Log.e("CartFragment", "Error updating product $productId quantity: ${t.message}")
                        }
                    })
                }
            } else {
                Log.e("CartFragment", "Error: New quantity for product $productId is negative")
            }
        }
    }

    private fun loadCartItems() {
        val sharedPreferences = requireContext().getSharedPreferences("app_prefs", Context.MODE_PRIVATE)
        val userId = sharedPreferences.getString("user_id", null)

        if (userId != null) {
            ApiClient.apiService.getCartItems(userId).enqueue(object : Callback<List<CartItem>> {
                override fun onResponse(call: Call<List<CartItem>>, response: Response<List<CartItem>>) {
                    if (response.isSuccessful && response.body() != null) {
                        cartItems = response.body()!!.toMutableList()
                        Log.d("CartFragment", "Loaded cart items: $cartItems")

                        cartAdapter = CartItemAdapter(requireContext(), cartItems) { removedItem ->
                            cartItems.remove(removedItem)
                            updateTotalPrice(cartItems)
                        }
                        binding.cartRecyclerView.adapter = cartAdapter
                        updateTotalPrice(cartItems)
                    } else {
                        Toast.makeText(requireContext(), "Failed to load cart items: ${response.code()}", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onFailure(call: Call<List<CartItem>>, t: Throwable) {
                    Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                }
            })
        } else {
            Toast.makeText(requireContext(), "User ID not found", Toast.LENGTH_SHORT).show()
        }
    }

    private fun updateTotalPrice(cartItems: List<CartItem>) {
        val total: Double = cartItems.sumOf { (it.productPrice ?: 0.0) * it.quantity }
        binding.totalPrice.text = "$${String.format("%.2f", total)}"
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
