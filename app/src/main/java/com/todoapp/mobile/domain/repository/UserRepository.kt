package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<RegisterResponseData>
    suspend fun login(request: LoginRequest): Result<LoginResponseData>
}
