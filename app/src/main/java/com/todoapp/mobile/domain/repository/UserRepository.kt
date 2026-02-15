package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.data.FCMTokenResponseData
import com.todoapp.mobile.data.model.network.data.GoogleLoginResponseData
import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.FCMTokenRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest

interface UserRepository {
    suspend fun register(request: RegisterRequest): Result<RegisterResponseData>
    suspend fun login(request: LoginRequest): Result<LoginResponseData>
    suspend fun googleLogin(token: String): Result<GoogleLoginResponseData>
    suspend fun facebookLogin(request: FacebookLoginRequest): Result<RegisterResponseData>
    suspend fun fcmToken(request: FCMTokenRequest): Result<FCMTokenResponseData>
    suspend fun syncPendingFcmToken(): Result<Unit>
}
