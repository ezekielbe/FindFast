package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.UserRequest
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path

interface ApiService {
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>

    @GET("user/{user_id}")
    fun getUser(@Path("user_id") userId: String): Call<UserResponse>

    @PUT("user/{user_id}")
    fun updateUser(@Path("user_id") userId: String, @Body user: UserRequest): Call<UserResponse>
}