package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import kotlinx.coroutines.flow.SharedFlow

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<AuthResponseData>
    suspend fun login(request: LoginRequest): Result<AuthResponseData>
    suspend fun googleLogin(token: String): Result<AuthResponseData>
    suspend fun facebookLogin(request: FacebookLoginRequest): Result<AuthResponseData>
    suspend fun getUserInfo(): Result<UserData>
}

interface AuthRepository {

    val events: SharedFlow<AuthEvent>
    suspend fun refresh(request: RefreshTokenRequest): Result<RefreshTokenData>

    suspend fun logout(): Result<Unit>

    suspend fun forceLogout(): Result<Unit>
}

sealed interface AuthEvent {

    data object Logout : AuthEvent

    data object ForceLogout : AuthEvent
}
