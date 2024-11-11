package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Models.CartItem
import com.aungsanoo.findfast.Models.Product
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.CartUpdateRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.DeleteCartRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserRequest
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.DELETE
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("user/{user_id}")
    fun getUser(@Path("user_id") userId: String): Call<UserResponse>

    @PUT("user/{user_id}")
    fun updateUser(@Path("user_id") userId: String, @Body user: UserRequest): Call<UserResponse>

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


    @GET("get_cart_items")
    fun getCartItems(@Query("user_id") userId: String): Call<List<CartItem>>

    @DELETE("delete_cart_item")
    fun deleteCartItem(
        @Query("user_id") userId: String,
        @Query("product_id") productId: String
    ): Call<ResponseBody>

    @POST("checkout")
    fun checkout(@Body userId: Map<String, String>): Call<ResponseBody>


}
