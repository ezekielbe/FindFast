package com.aungsanoo.findfast.Fragments

import android.graphics.Color
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.ProductUpdateRequest
import com.aungsanoo.findfast.databinding.FragmentAdminProductDetailBinding
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.R
import com.bumptech.glide.Glide
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductDetailFragment : Fragment() {

    private var _binding: FragmentAdminProductDetailBinding? = null
    private val binding get() = _binding!!
    private var currentQty = 0
    private lateinit var productId: String

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Get productId from arguments
        productId = arguments?.getString("productId") ?: ""

        // Load product details from server
        loadProductDetails(productId)

        binding.increaseBtn.setOnClickListener {
            currentQty++
            updateQuantityText()
        }

        binding.decreaseBtn.setOnClickListener {
            if (currentQty > 1) {
                currentQty--
                updateQuantityText()
            }
        }

        binding.saveUpdateBtn.setOnClickListener {
            updateProduct(productId)
        }

        binding.deleteBtn.setOnClickListener {
            deleteProduct(productId)
        }
    }

    private fun loadProductDetails(productId: String) {
        ApiClient.apiService.getProductById(productId).enqueue(object : Callback<Product> {
            override fun onResponse(call: Call<Product>, response: Response<Product>) {
                if (response.isSuccessful) {
                    val product = response.body()
                    product?.let {
                        // Set values to UI components
                        binding.productName.setText(it.name)
                        binding.productPrice.setText(it.price.toString())
                        binding.productDescription.setText(it.description)
                        binding.productMaterial.setText(it.material.joinToString(", "))
                        binding.productColor.setText(it.color.joinToString(", "))
                        binding.productSize.setText(it.size.joinToString(", "))
                        binding.productAvailability.setText(if (it.availability) "Available" else "Out of Stock")
                        binding.basePrice.setText(it.basePrice.toString())
                        binding.imageLink.setText(it.imageUrl)
                        currentQty = it.qty
                        updateQuantityText()

                        Glide.with(binding.productImage.context)
                            .load(it.imageUrl)
                            .placeholder(R.drawable.nopic)
                            .error(R.drawable.nopic)
                            .into(binding.productImage)
                    }
                } else {
                    Toast.makeText(requireContext(), "Failed to load product details", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Product>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateQuantityText() {
        binding.qtyTxt.text = currentQty.toString()

        // Change text color based on quantity
        binding.qtyTxt.setTextColor(
            when {
                currentQty > 10 -> Color.GREEN
                currentQty in 4..10 -> Color.parseColor("#FFA500") // Orange color
                else -> Color.RED
            }
        )
    }

    private fun deleteProduct(productId: String) {
        ApiClient.apiService.deleteProduct(productId).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Product deleted successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to delete product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    private fun updateProduct(productId: String) {
        val updatedName = binding.productName.text.toString()
        val updatedPrice = binding.productPrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedDescription = binding.productDescription.text.toString()
        val updatedMaterial = binding.productMaterial.text.toString()
        val updatedColor = binding.productColor.text.toString()
        val updatedSize = binding.productSize.text.toString()
        val updatedAvailability = binding.productAvailability.text.toString().equals("Available", ignoreCase = true)
        val updatedBasePrice = binding.basePrice.text.toString().toDoubleOrNull() ?: 0.0
        val updatedImageUrl = binding.imageLink.text.toString()

        val updateRequest = ProductUpdateRequest(
            productId = productId,
            name = updatedName,
            basePrice = updatedBasePrice,
            price = updatedPrice,
            description = updatedDescription,
            material = updatedMaterial,
            color = updatedColor,
            size = updatedSize,
            availability = updatedAvailability,
            imageUrl = updatedImageUrl
        )

        ApiClient.apiService.updateProduct(productId, updateRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Product updated successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to update product", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    companion object {
        fun newInstance(
            productId: String,
            productName: String,
            productPrice: Double,  // Add this line
            productDescription: String,
            productMaterial: String,
            productColor: String,
            productSize: String,
            productAvailability: String,
            basePrice: Double,
            qty: Int
        ) = AdminProductDetailFragment().apply {
            arguments = Bundle().apply {
                putString("productId", productId)
                putString("productName", productName)
                putDouble("productPrice", productPrice) // Add this line
                putString("productDescription", productDescription)
                putString("productMaterial", productMaterial)
                putString("productColor", productColor)
                putString("productSize", productSize)
                putString("productAvailability", productAvailability)
                putDouble("productBasePrice", basePrice)
                putInt("qty", qty)
            }
        }
    }

}
