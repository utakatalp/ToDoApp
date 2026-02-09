package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.model.network.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ToDoApi {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): Response<BaseResponse<RegisterResponseData?>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<BaseResponse<LoginResponseData?>>
}
