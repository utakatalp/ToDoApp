package com.todoapp.mobile.data.source.remote.interceptor

import android.util.Log
import com.todoapp.mobile.domain.repository.SessionPreferences
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject
constructor(
    private val sessionPreferences: SessionPreferences,
) : Interceptor {
    private val noAuthPaths =
        listOf(
            "/auth/register",
            "/auth/login",
            "/auth/google",
        )

    override fun intercept(chain: Interceptor.Chain): Response {
        val original = chain.request()

        if (noAuthPaths.any { original.url.encodedPath.contains(it) }) {
            return chain.proceed(original)
        }

        val token =
            runBlocking {
                sessionPreferences.getAccessToken()
            }
        if (token == null) {
            Log.w(
                "AuthInterceptor",
                "Access token is null — request will be unauthenticated: ${original.url.encodedPath}",
            )
        }
        val request =
            if (token != null) {
                original
                    .newBuilder()
                    .addHeader("Authorization", "Bearer $token")
                    .build()
            } else {
                original
            }
        return chain.proceed(request)
    }
}
