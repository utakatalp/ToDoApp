package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.RegisterRequest

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<RegisterResponseData>
}
