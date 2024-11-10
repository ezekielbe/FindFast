package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("products")
    fun getProducts(): Call<List<Product>>

    @GET("products")
    fun searchProducts(
        @Query("name") name: String?,
        @Query("material") material: String?,
        @Query("priceRange") priceRange: String?
    ): Call<List<Product>>

}
