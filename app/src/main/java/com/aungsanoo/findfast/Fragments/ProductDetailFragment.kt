package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.databinding.FragmentProductDetailBinding
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!
    private var currentQty = 1  // Default quantity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentProductDetailBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val productName = arguments?.getString("productName")
        val productPrice = arguments?.getDouble("productPrice", 0.0)
        val productDescription = arguments?.getString("productDescription")
        val productMaterial = arguments?.getString("productMaterial")
        val productColor = arguments?.getString("productColor")
        val productSize = arguments?.getString("productSize")
        val productAvailability = arguments?.getBoolean("productAvailability", true)

        binding.productName.text = productName
        binding.productPrice.text = "Price: $$productPrice"
        binding.productDescription.text = productDescription

        binding.qtyTxt.text = currentQty.toString()

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

        binding.addToCartBtn.setOnClickListener {
            val userId = ""
            val productId = ""
            val quantity = binding.qtyTxt.text.toString().toInt()

            addToCart(userId, productId, quantity)
        }

    }



    private fun addToCart(userId: String, productId: String, quantity: Int) {
        val requestData = mapOf(
            "user_id" to userId,
            "product_id" to productId,
            "quantity" to quantity
        )
        ApiClient.apiService.updateCart(userId, productId, quantity).enqueue(object : Callback<ResponseBody> {
            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                if (response.isSuccessful) {
                    Toast.makeText(requireContext(), "Added to cart successfully!", Toast.LENGTH_SHORT).show()
                } else {
                    Toast.makeText(requireContext(), "Failed to add to cart", Toast.LENGTH_SHORT).show()
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
            productName: String,
            productPrice: Double,
            productDescription: String,
            productMaterial: String,
            productColor: String,
            productSize: String,
            productAvailability: Boolean
        ) = ProductDetailFragment().apply {
            arguments = Bundle().apply {
                putString("productName", productName)
                putDouble("productPrice", productPrice)
                putString("productDescription", productDescription)
                putString("productMaterial", productMaterial)
                putString("productColor", productColor)
                putString("productSize", productSize)
                putBoolean("productAvailability", productAvailability)
            }
        }
    }
}
