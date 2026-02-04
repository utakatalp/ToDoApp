package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.request.RegisterRequest
import com.todoapp.mobile.data.model.response.RegisterResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface ToDoApi {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): RegisterResponse
}
