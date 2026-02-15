package com.todoapp.mobile.data.repository

import android.util.Log
import com.google.firebase.messaging.FirebaseMessaging
import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.FCMTokenResponseData
import com.todoapp.mobile.data.model.network.data.GoogleLoginResponseData
import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.FCMTokenRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.domain.repository.FCMTokenPreferences
import com.todoapp.mobile.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import okio.IOException
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val todoApi: ToDoApi,
    private val fcmTokenPreferences: FCMTokenPreferences,
) : UserRepository {

    override suspend fun register(request: RegisterRequest): Result<RegisterResponseData> {
        val result = handleRequest { todoApi.register(request) }
        return result
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponseData> {
        val result = handleRequest { todoApi.login(request) }
        return result
    }

    override suspend fun googleLogin(token: String): Result<GoogleLoginResponseData> {
        val result = handleRequest { todoApi.googleLogin(GoogleLoginRequest(token = token)) }
        return result
    }

    override suspend fun facebookLogin(request: FacebookLoginRequest): Result<RegisterResponseData> {
        val result = handleRequest { todoApi.facebookLogin(request) }
        return result
    }

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
}
