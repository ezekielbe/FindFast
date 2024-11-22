package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.ProductUpdateRequest
import com.aungsanoo.findfast.databinding.FragmentAdminProductDetailBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminProductDetailFragment : Fragment() {

    private var _binding: FragmentAdminProductDetailBinding? = null
    private val binding get() = _binding!!
    private var currentQty = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Extract arguments
        val productId = arguments?.getString("productId") ?: ""
        val productName = arguments?.getString("productName") ?: ""
        val productPrice = arguments?.getDouble("productPrice") ?: 0.0
        val productDescription = arguments?.getString("productDescription") ?: ""
        val productMaterial = arguments?.getString("productMaterial") ?: ""
        val productColor = arguments?.getString("productColor") ?: ""
        val productSize = arguments?.getString("productSize") ?: ""
        val productAvailability = arguments?.getString("productAvailability") ?: "false"
        val productBasePrice = arguments?.getDouble("productBasePrice") ?: 0.0  // Retrieve basePrice

        println("Base Price retrieved in onViewCreated: $productBasePrice") // Log basePrice

        // Set values to UI components
        binding.productName.setText(productName)
        binding.productPrice.setText(productPrice.toString())
        binding.productDescription.setText(productDescription)
        binding.productMaterial.setText(productMaterial)
        binding.productColor.setText(productColor)
        binding.productSize.setText(productSize)
        binding.productAvailability.setText(productAvailability)
        binding.basePrice.setText(productBasePrice.toString())  // Set basePrice to EditText

        // Setup button click listeners
        binding.increaseBtn.setOnClickListener {
            currentQty++
            binding.qtyTxt.text = currentQty.toString()
        }

        binding.decreaseBtn.setOnClickListener {
            if (currentQty > 1) {
                currentQty--
                binding.qtyTxt.text = currentQty.toString()
            }
        }

        binding.saveUpdateBtn.setOnClickListener {
            updateProduct(productId)
        }

        binding.deleteBtn.setOnClickListener {
            deleteProduct(productId)
        }
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

        val updateRequest = ProductUpdateRequest(
            productId = productId,
            name = updatedName,
            basePrice = updatedBasePrice,
            price = updatedPrice,
            description = updatedDescription,
            material = updatedMaterial,
            color = updatedColor,
            size = updatedSize,
            availability = updatedAvailability
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
            productPrice: Double,
            productDescription: String,
            productMaterial: String,
            productColor: String,
            productSize: String,
            productAvailability: String,
            basePrice: Double
        ) = AdminProductDetailFragment().apply {
            arguments = Bundle().apply {
                putString("productId", productId)
                putString("productName", productName)
                putDouble("productPrice", productPrice)
                putString("productDescription", productDescription)
                putString("productMaterial", productMaterial)
                putString("productColor", productColor)
                putString("productSize", productSize)
                putString("productAvailability", productAvailability)
                putDouble("productBasePrice", basePrice)  // Add basePrice to arguments
            }
        }
    }
}
