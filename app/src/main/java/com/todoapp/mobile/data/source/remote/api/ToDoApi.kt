package com.todoapp.mobile.data.source.remote.api

import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.FCMTokenResponseData
import com.todoapp.mobile.data.model.network.data.GroupActivityDataList
import com.todoapp.mobile.data.model.network.data.GroupData
import com.todoapp.mobile.data.model.network.data.GroupSummaryDataList
import com.todoapp.mobile.data.model.network.data.GroupTaskData
import com.todoapp.mobile.data.model.network.data.GroupTaskListData
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.TaskData
import com.todoapp.mobile.data.model.network.data.TaskListData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.CreateGroupRequest
import com.todoapp.mobile.data.model.network.request.FCMTokenRequest
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.GroupTaskRequest
import com.todoapp.mobile.data.model.network.request.GroupTaskUpdateRequest
import com.todoapp.mobile.data.model.network.request.InviteMemberRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.model.network.request.TaskRequest
import com.todoapp.mobile.data.model.network.request.TransferOwnershipRequest
import com.todoapp.mobile.data.model.network.request.UpdateGroupRequest
import com.todoapp.mobile.data.model.network.request.UpdateUserRequest
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
        @Body request: TaskRequest,
    ): Response<BaseResponse<TaskData?>>

    @PUT("tasks")
    suspend fun updateTask(
        @Body request: TaskRequest,
    ): Response<BaseResponse<TaskData?>>

    @GET("tasks")
    suspend fun getTasks(
        @Query("familyGroupId") familyGroupId: Long? = null,
    ): Response<BaseResponse<TaskListData?>>

    @GET("tasks/{id}")
    suspend fun getTaskById(
        @Path("id") id: Long,
    ): Response<BaseResponse<TaskData?>>

    @DELETE("tasks/{id}")
    suspend fun deleteTask(
        @Path("id") taskId: Long,
    ): Response<BaseResponse<Unit?>>

    @POST("auth/facebook")
    suspend fun facebookLogin(
        @Body request: FacebookLoginRequest,
    ): Response<BaseResponse<AuthResponseData?>>

    @POST("devices/fcm-token")
    suspend fun fcmToken(
        @Body request: FCMTokenRequest,
    ): Response<BaseResponse<FCMTokenResponseData?>>

    @GET("users/me")
    suspend fun getUserInfo(): Response<BaseResponse<UserData?>>

    @PUT("users/me")
    suspend fun updateUser(
        @Body request: UpdateUserRequest,
    ): Response<BaseResponse<UserData?>>

    @retrofit2.http.Multipart
    @POST("users/me/avatar")
    suspend fun uploadAvatar(
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part,
    ): Response<BaseResponse<UserData?>>

    @retrofit2.http.Multipart
    @POST("tasks/{taskId}/photos")
    suspend fun uploadTaskPhoto(
        @Path("taskId") taskId: Long,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part,
    ): Response<BaseResponse<com.todoapp.mobile.data.model.network.data.TaskPhotoData?>>

    @DELETE("tasks/{taskId}/photos/{photoId}")
    suspend fun deleteTaskPhoto(
        @Path("taskId") taskId: Long,
        @Path("photoId") photoId: Long,
    ): Response<BaseResponse<Unit?>>

    @POST("family-groups")
    suspend fun createGroup(
        @Body request: CreateGroupRequest,
    ): Response<BaseResponse<GroupData?>>

    @GET("family-groups")
    suspend fun getGroups(): Response<BaseResponse<GroupSummaryDataList?>>

    @DELETE("family-groups/{id}")
    suspend fun deleteGroup(
        @Path("id") id: Long,
    ): Response<BaseResponse<Unit?>>

    @GET("family-groups/{id}")
    suspend fun getGroupDetail(
        @Path("id") id: Long,
    ): Response<BaseResponse<GroupData?>>

    @PUT("family-groups")
    suspend fun updateGroup(
        @Body request: UpdateGroupRequest,
    ): Response<BaseResponse<GroupData?>>

    @POST("family-groups/members")
    suspend fun inviteMember(
        @Body request: InviteMemberRequest,
    ): Response<BaseResponse<Unit?>>

    @DELETE("family-groups/members/{groupId}/{userId}")
    suspend fun removeMember(
        @Path("groupId") id: Long,
        @Path("userId") userId: Long,
    ): Response<BaseResponse<Unit?>>

    @PUT("family-groups/{groupId}/transfer-ownership")
    suspend fun transferOwnership(
        @Path("groupId") groupId: Long,
        @Body request: TransferOwnershipRequest,
    ): Response<BaseResponse<Unit?>>

    @GET("family-groups/{id}/activity")
    suspend fun getGroupActivity(
        @Path("id") id: Long,
    ): Response<BaseResponse<GroupActivityDataList?>>

    @GET("family-groups/{id}/tasks")
    suspend fun getGroupTasks(
        @Path("id") id: Long,
    ): Response<BaseResponse<GroupTaskListData?>>

    @POST("family-groups/{id}/tasks")
    suspend fun createGroupTask(
        @Path("id") id: Long,
        @Body request: GroupTaskRequest,
    ): Response<BaseResponse<GroupTaskData?>>

    @DELETE("family-groups/{groupId}/tasks/{taskId}")
    suspend fun deleteGroupTask(
        @Path("groupId") groupId: Long,
        @Path("taskId") taskId: Long,
    ): Response<BaseResponse<Unit?>>

    @PUT("family-groups/{groupId}/tasks/{taskId}")
    suspend fun updateGroupTask(
        @Path("groupId") groupId: Long,
        @Path("taskId") taskId: Long,
        @Body request: GroupTaskUpdateRequest,
    ): Response<BaseResponse<GroupTaskData?>>

    @retrofit2.http.Multipart
    @POST("family-groups/{groupId}/avatar")
    suspend fun uploadGroupAvatar(
        @Path("groupId") groupId: Long,
        @retrofit2.http.Part file: okhttp3.MultipartBody.Part,
    ): Response<BaseResponse<GroupData?>>
}

interface TodoAuthApi {
    @POST("auth/refresh")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest,
    ): Response<BaseResponse<RefreshTokenData?>>
}
