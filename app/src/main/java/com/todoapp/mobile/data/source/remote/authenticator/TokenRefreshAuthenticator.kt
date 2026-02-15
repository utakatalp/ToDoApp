package com.todoapp.mobile.data.source.remote.authenticator

import android.util.Log
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.SessionPreferences
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import javax.inject.Inject

class TokenRefreshAuthenticator @Inject constructor(
    private val sessionPreferences: SessionPreferences,
    private val mutex: Mutex,
    private val authRepository: AuthRepository
) : Authenticator {
    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) return null
        Log.d("TokenRefreshAuthenticator", "authenticate: $response")
        return runBlocking {
            Log.d("TokenRefresh", sessionPreferences.getRefreshToken().toString())

            mutex.withLock {
                val currentAccessToken = sessionPreferences.getAccessToken()
                val requestAccessToken = response.request.header("Authorization")?.removePrefix("Bearer ")
                if (!currentAccessToken.isNullOrBlank() && currentAccessToken != requestAccessToken) {
                    return@runBlocking response.request.newBuilder()
                        .header("Authorization", "Bearer $currentAccessToken")
                        .build()
                }

                val refreshed = authRepository.refresh(
                    RefreshTokenRequest(
                        sessionPreferences.getRefreshToken().orEmpty()
                    )
                )
                Log.d("TokenRefresh", refreshed.toString())
                if (refreshed.isSuccess) {
                    val newAccessToken = refreshed.getOrThrow().accessToken
                    val newRefreshToken = refreshed.getOrThrow().refreshToken
                    val newExpiresIn = refreshed.getOrThrow().expiresIn
                    Log.d("TokenRefresh", newAccessToken)
                    sessionPreferences.setAccessToken(newAccessToken)
                    sessionPreferences.setRefreshToken(newRefreshToken)
                    sessionPreferences.setExpiresAt(newExpiresIn)
                    response.request.newBuilder()
                        .header("Authorization", "Bearer $newAccessToken")
                        .build()
                } else {
                    null
                }
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var r: Response? = response
        var count = 1
        while (r?.priorResponse != null) {
            count++
            r = r.priorResponse
        }
        return count
    }
}
