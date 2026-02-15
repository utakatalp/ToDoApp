package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.network.data.FCMTokenResponseData
import com.todoapp.mobile.data.model.network.data.GoogleLoginResponseData
import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.request.FCMTokenRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface ToDoApi {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<BaseResponse<RegisterResponseData?>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<BaseResponse<LoginResponseData?>>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest,
    ): Response<BaseResponse<GoogleLoginResponseData?>>

    @POST("tasks")
    suspend fun addTask(
        @Body request: TaskRequest,
    ): Response<BaseResponse<TaskData?>>

    @POST("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<TaskData?>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<Unit?>>

    @POST("auth/facebook")
    suspend fun facebookLogin(
        @Body request: FacebookLoginRequest,
    ): Response<BaseResponse<RegisterResponseData?>>

    @POST("devices/fcm-token")
    suspend fun fcmToken(
        @Body request: FCMTokenRequest,
    ): Response<BaseResponse<FCMTokenResponseData?>>
}
