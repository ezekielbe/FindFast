package com.aungsanoo.findfast.Utils.API

import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.LoginResponse
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterRequest
import com.aungsanoo.findfast.Utils.API.RequestResponseModels.RegisterResponse
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {
    @POST("register")
    fun register(@Body registerRequest: RegisterRequest): Call<RegisterResponse>

    @POST("login")
    fun login(@Body loginRequest: LoginRequest): Call<LoginResponse>
}