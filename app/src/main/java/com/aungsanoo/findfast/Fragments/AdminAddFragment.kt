package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.ProductRequest
import com.aungsanoo.findfast.databinding.FragmentAdminAddBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AdminAddFragment : Fragment() {

    private var _binding: FragmentAdminAddBinding? = null
    private val binding get() = _binding!!
    private var currentQty = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentAdminAddBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Set up buttons for quantity adjustment
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

        // Set up button to add product
        binding.addItemButton.setOnClickListener {

            val productName = binding.productName.text.toString()
            val productPrice = binding.productPrice.text.toString().toDoubleOrNull()
            val productDescription = binding.productDescription.text.toString()
            val productMaterial = binding.productMaterial.text.toString().split(",").map { it.trim() }
            val productColor = binding.productColor.text.toString().split(",").map { it.trim() }
            val productSize = binding.productSize.text.toString().split(",").map { it.trim() }
            val productAvailability = binding.productAvailability.text.toString().equals("In Stock", ignoreCase = true)
            val productQty = binding.qtyTxt.text.toString().toIntOrNull() ?: 0
            val productAisle = binding.productAisle.text.toString()
            val productType = binding.productType.text.toString()
            val productShelf = binding.productShelf.text.toString()
            val productBin = binding.productBin.text.toString()
            val productImageUrl = binding.productImageLink.text.toString()

            if (productName.isNotEmpty() && productPrice != null && productQty > 0 &&
                productAisle.isNotEmpty() && productType.isNotEmpty() &&
                productShelf.isNotEmpty() && productBin.isNotEmpty() && productImageUrl.isNotEmpty()) {
                addProduct(
                    productName,
                    productPrice,
                    productDescription,
                    productMaterial,
                    productColor,
                    productSize,
                    productAvailability,
                    productQty,
                    productAisle,
                    productType,
                    productShelf,
                    productBin,
                    productImageUrl
                )
            } else {
                Toast.makeText(requireContext(), "Please fill out all fields correctly", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun addProduct(
        name: String,
        price: Double,
        description: String,
        material: List<String>,
        color: List<String>,
        size: List<String>,
        availability: Boolean,
        qty: Int,
        aisle: String,
        type: String,
        shelf: String,
        bin: String,
        imageUrl: String
    ) {
        val productRequest = ProductRequest(
            name = name,
            price = price,
            description = description,
            material = material.toTypedArray(),
            color = color.toTypedArray(),
            size = size.toTypedArray(),
            availability = availability,
            qty = qty,
            aisle = aisle,
            type = type,
            shelf = shelf,
            bin = bin,
            imageUrl = imageUrl
        )

        ApiClient.apiService.addProduct(productRequest).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Product added successfully!", Toast.LENGTH_SHORT).show()
                    parentFragmentManager.popBackStack()
                } else {
                    Toast.makeText(requireContext(), "Failed to add product", Toast.LENGTH_SHORT).show()
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
}
