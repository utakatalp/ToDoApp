package com.todoapp.mobile.data.repository

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.GoogleLoginResponseData
import com.todoapp.mobile.data.model.network.data.LoginResponseData
import com.todoapp.mobile.data.model.network.data.RegisterResponseData
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val todoApi: ToDoApi,
) : UserRepository {

    override suspend fun register(request: RegisterRequest): Result<RegisterResponseData> {
        return handleRequest { todoApi.register(request) }
    }

    override suspend fun login(request: LoginRequest): Result<LoginResponseData> {
        return handleRequest { todoApi.login(request) }
    }

    override suspend fun googleLogin(token: String): Result<GoogleLoginResponseData> {
        return handleRequest { todoApi.googleLogin(GoogleLoginRequest(token = token)) }
    }

    override suspend fun facebookLogin(request: FacebookLoginRequest): Result<RegisterResponseData> {
        return handleRequest { todoApi.facebookLogin(request) }
    }
}
