package com.todoapp.mobile.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.FCMTokenResponseData
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.FCMTokenRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.data.source.remote.api.TodoAuthApi
import com.todoapp.mobile.domain.repository.AuthEvent
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.FCMTokenPreferences
import com.todoapp.mobile.domain.repository.UserRepository
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.tasks.await
import okio.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val todoApi: ToDoApi,
    private val fcmTokenPreferences: FCMTokenPreferences,
) : UserRepository {

    override suspend fun fcmToken(request: FCMTokenRequest): Result<FCMTokenResponseData> {
        return handleRequest { todoApi.fcmToken(request) }
    }

    override suspend fun syncPendingFcmToken(): Result<Unit> {
        var pendingToken = fcmTokenPreferences.getPendingToken()

        if (pendingToken.isNullOrBlank()) {
            pendingToken = try {
                FirebaseMessaging.getInstance().token.await()
            } catch (e: IOException) {
                Log.d("FCM_SYNC", "Failed to get Firebase token", e)
                return Result.success(Unit)
            }
        }

        val lastSentToken = fcmTokenPreferences.getLastSentToken()
        val deviceId = fcmTokenPreferences.getDeviceId()
        val deviceName = fcmTokenPreferences.getDeviceName()

        if (pendingToken.isNullOrBlank()) return Result.success(Unit)

        if (pendingToken == lastSentToken) {
            fcmTokenPreferences.clearPendingToken()
            return Result.success(Unit)
        }

        val apiResult: Result<FCMTokenResponseData> = fcmToken(
            FCMTokenRequest(
                token = pendingToken,
                deviceId = deviceId,
                deviceName = deviceName
            )
        )

        apiResult
            .onSuccess {
                fcmTokenPreferences.setLastSentToken(pendingToken)
                fcmTokenPreferences.clearPendingToken()
            }
            .onFailure { e ->
                Log.e("FCM_SYNC", "Token send FAILED. Pending token preserved.", e)
            }

        return apiResult.map {}
    }

    override suspend fun register(request: RegisterRequest): Result<AuthResponseData> {
        return handleRequest { todoApi.register(request) }
    }

    override suspend fun login(request: LoginRequest): Result<AuthResponseData> {
        return handleRequest { todoApi.login(request) }
    }

    override suspend fun googleLogin(token: String): Result<AuthResponseData> {
        return handleRequest { todoApi.googleLogin(GoogleLoginRequest(token = token)) }
    }

    override suspend fun facebookLogin(request: FacebookLoginRequest): Result<AuthResponseData> {
        return handleRequest { todoApi.facebookLogin(request) }
    }

    override suspend fun getUserInfo(): Result<UserData> {
        return handleRequest { todoApi.getUserInfo() }
    }
}

class AuthRepositoryImpl @Inject constructor(
    private val authApi: TodoAuthApi,
) : AuthRepository {

    private val _events = MutableSharedFlow<AuthEvent>(replay = 0)
    override val events: SharedFlow<AuthEvent> = _events.asSharedFlow()

    override suspend fun logout(): Result<Unit> {
        _events.emit(AuthEvent.Logout)
        return Result.success(Unit)
    }

    override suspend fun forceLogout(): Result<Unit> {
        _events.emit(AuthEvent.ForceLogout)
        return Result.success(Unit)
    }

    override suspend fun refresh(request: RefreshTokenRequest): Result<RefreshTokenData> {
        return handleRequest { authApi.refreshToken(request) }
    }
}
