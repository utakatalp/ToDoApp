package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponseData>
    suspend fun login(request: LoginRequest): Result<AuthResponseData>
    suspend fun googleLogin(token: String): Result<AuthResponseData>
    suspend fun facebookLogin(request: FacebookLoginRequest): Result<AuthResponseData>
    suspend fun getUserInfo(): Result<UserData>
}

interface AuthRepository {
    suspend fun refresh(request: RefreshTokenRequest): Result<RefreshTokenData>
}
