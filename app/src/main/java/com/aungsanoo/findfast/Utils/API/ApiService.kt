package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Models.*
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.*
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

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

    @GET("search")
    fun searchProducts(
        @Query("name") name: String?,
        @Query("material") material: String?,
        @Query("priceRange") priceRange: String?
    ): Call<List<Product>>
    @GET("search_admin_product")
    fun searchAdminProducts(
        @Query("name") name: String?,
        @Query("material") material: String?,
        @Query("priceRange") priceRange: String?
    ): Call<List<Product>>


    @POST("update_cart")
    fun updateCart(@Body request: CartUpdateRequest): Call<ResponseBody>

    @GET("products/{id}")
    fun getProductById(@Path("id") productId: String): Call<Product>

    @GET("get_cart_items")
    fun getCartItems(@Query("user_id") userId: String): Call<List<CartItem>>

    @DELETE("delete_cart_item")
    fun deleteCartItem(
        @Query("user_id") userId: String,
        @Query("product_id") productId: String
    ): Call<ResponseBody>

    @POST("checkout")
    fun checkout(@Body userId: Map<String, String>): Call<ResponseBody>

    @PUT("products/{id}")
    fun updateProduct(@Path("id") productId: String, @Body request: ProductUpdateRequest): Call<ResponseBody>

    @DELETE("products/{id}")
    fun deleteProduct(@Path("id") productId: String): Call<ResponseBody>

    @POST("products")
    fun addProduct(@Body product: ProductRequest): Call<ResponseBody>

    @GET("transactions")
    fun transactions(): Call<List<Transaction>>

    @POST("transactions_by_date")
    fun transactionsByDate(@Body request: TransactionsByDateRequest): Call<List<Transaction>>

    @GET("transactions/{id}")
    fun transactionsByUser(@Path("id") userId: String): Call<List<Transaction>>

    @PUT("transactions/{id}/{status}")
    fun updateOrder(@Path("id") transactionId: String, @Path("status") status: Int): Call<Transaction>

    @PUT("cancel_order/{id}")
    fun cancelOrder(@Path("id") transactionId: String, @Body request: CancelOrderRequest): Call<CancelOrderResponse>

    @POST("financial_report")
    fun financialReport(@Body request: FinancialReportRequest): Call<FinancialReportResponse>

    @PUT("products/{id}/quantity")
    fun updateProductQuantity(
        @Path("id") productId: String,
        @Query("quantity") newQuantity: Int
    ): Call<ResponseBody>
}
