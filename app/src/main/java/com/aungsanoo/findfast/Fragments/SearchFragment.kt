package com.aungsanoo.findfast.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.aungsanoo.findfast.Adapters.ProductAdapter
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.R
import com.aungsanoo.findfast.Utils.API.ApiClient
import com.aungsanoo.findfast.databinding.FragmentSearchBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class SearchFragment : Fragment() {

    private var _binding: FragmentSearchBinding? = null
    private val binding get() = _binding!!
    private lateinit var productAdapter: ProductAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSearchBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        binding.searchRecyclerView.layoutManager = LinearLayoutManager(requireContext())
        setupMaterialSpinner()

        binding.searchButton.setOnClickListener {
            performSearch()
        }
    }

    private fun setupMaterialSpinner() {
        val materials = resources.getStringArray(R.array.material_options)
        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, materials)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.materialSpinner.adapter = adapter
    }

    private fun performSearch() {
        val searchQuery = binding.searchInput.text.toString().trim()
        val selectedMaterial = binding.materialSpinner.selectedItem.toString()
        val priceRange = binding.priceRangeInput.text.toString().trim()

        ApiClient.apiService.searchProducts(
            name = if (searchQuery.isNotEmpty()) searchQuery else null,
            material = if (selectedMaterial != "All") selectedMaterial else null,
            priceRange = if (priceRange.isNotEmpty()) priceRange else null
        ).enqueue(object : Callback<List<Product>> {
            override fun onResponse(call: Call<List<Product>>, response: Response<List<Product>>) {
                if (response.isSuccessful && response.body() != null) {
                    val products = response.body()!!
                    if (products.isNotEmpty()) {
                        productAdapter = ProductAdapter(products, requireActivity())
                        binding.searchRecyclerView.adapter = productAdapter
                    } else {
                        Toast.makeText(requireContext(), "No products found", Toast.LENGTH_SHORT).show()
                    }
                } else {
                    Toast.makeText(requireContext(), "Search failed", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<List<Product>>, t: Throwable) {
                Toast.makeText(requireContext(), "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                t.printStackTrace()
            }
        })
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
