package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupMemberData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.data.TaskListData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.AddMemberRequest
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.request.TaskUpdateRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest
import com.todoapp.mobile.data.model.network.response.BaseResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface ToDoApi {
    @POST("auth/register")
    suspend fun register(
        @Body request: RegisterRequest,
    ): Response<BaseResponse<AuthResponseData?>>

    @POST("auth/login")
    suspend fun login(
        @Body request: LoginRequest,
    ): Response<BaseResponse<AuthResponseData?>>

    @POST("auth/google")
    suspend fun googleLogin(
        @Body request: GoogleLoginRequest,
    ): Response<BaseResponse<AuthResponseData?>>

    @POST("tasks")
    suspend fun addTask(
        @Body request: TaskRequest
    ): Response<BaseResponse<TaskData?>>

    @PUT("tasks")
    suspend fun updateTask(
        @Body request: TaskUpdateRequest
    ): Response<BaseResponse<TaskData?>>

    @GET("tasks")
    suspend fun getTasks(
        @Query("familyGroupId") familyGroupId: Long? = null
    ): Response<BaseResponse<TaskListData?>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<Unit?>>

    @POST("auth/facebook")
    suspend fun facebookLogin(
        @Body request: FacebookLoginRequest
    ): Response<BaseResponse<AuthResponseData?>>

    @GET("users/me")
    suspend fun getUserInfo(): Response<BaseResponse<UserData?>>

    @POST("family-groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest
    ): Response<BaseResponse<GroupData?>>

    @GET("family-groups")
    suspend fun getGroups(): Response<BaseResponse<GroupSummaryDataList?>>

    @DELETE("family-groups/{id}")
    suspend fun deleteGroup(
        @Path("id") id: Long
    ): Response<BaseResponse<Unit?>>

    @GET("family-groups/{id}")
    suspend fun getGroupDetails(
        @Path("id") id: Long
    ): Response<BaseResponse<GroupData?>>

    @PUT("family-groups")
    suspend fun updateGroup(
        @Body request: UpdateGroupRequest
    ): Response<BaseResponse<GroupData?>>

    @POST("family-groups/members")
    suspend fun addMember(
        @Body request: AddMemberRequest
    ): Response<BaseResponse<GroupMemberData?>>

    @DELETE("family-groups/members/{groupId}/{userId}")
    suspend fun removeMember(
        @Path("userId") userId: Long,
        @Path("groupId") groupId: Long
    ): Response<BaseResponse<Boolean?>>
}

interface TodoAuthApi {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): Response<BaseResponse<RefreshTokenData?>>
}
