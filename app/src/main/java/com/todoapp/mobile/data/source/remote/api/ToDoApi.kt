package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.data.TaskData
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
        @Body request: RegisterRequest
    ): Response<BaseResponse<RegisterResponseData?>>

    @POST("tasks")
    suspend fun addTask(
        @Body request: TaskRequest
    ): Response<BaseResponse<TaskData?>>

    @POST("tasks/{id}")
    suspend fun updateTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<TaskData?>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<Unit?>>
}
