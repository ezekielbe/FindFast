package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.aungsanoo.findfast.databinding.FragmentProductDetailBinding

class ProductDetailFragment : Fragment() {

    private var _binding: FragmentProductDetailBinding? = null
    private val binding get() = _binding!!

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
        binding.productMaterial.text = "Material: $productMaterial"
        binding.productColor.text = "Color: $productColor"
        binding.productSize.text = "Size: $productSize"
        binding.productAvailability.text = if (productAvailability == true) "Availability: In Stock" else "Availability: Out of Stock"
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
