package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Models.CartItem
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CartUpdateRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
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

    @POST("update_cart")
    fun updateCart(
        @Body request: CartUpdateRequest
    ): Call<ResponseBody>


    @GET("get_cart")
    fun getCartItems(@Query("user_id") userId: String): Call<List<CartItem>>

}
