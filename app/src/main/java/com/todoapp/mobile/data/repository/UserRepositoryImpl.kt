package com.todoapp.mobile.data.repository

import com.todoapp.mobile.common.handleRequest
import com.todoapp.mobile.data.model.network.data.AuthResponseData
import com.todoapp.mobile.data.model.network.data.RefreshTokenData
import com.todoapp.mobile.data.model.network.data.UserData
import com.todoapp.mobile.data.model.network.request.FacebookLoginRequest
import com.todoapp.mobile.data.model.network.request.GoogleLoginRequest
import com.todoapp.mobile.data.model.network.request.LoginRequest
import com.todoapp.mobile.data.model.network.request.RefreshTokenRequest
import com.todoapp.mobile.data.model.network.request.RegisterRequest
import com.todoapp.mobile.data.source.remote.api.ToDoApi
import com.todoapp.mobile.data.source.remote.api.TodoAuthApi
import com.todoapp.mobile.domain.repository.AuthRepository
import com.todoapp.mobile.domain.repository.UserRepository
import javax.inject.Inject

class UserRepositoryImpl @Inject constructor(
    private val todoApi: ToDoApi,
) : UserRepository {

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
    private val authApi: TodoAuthApi
) : AuthRepository {
    override suspend fun refresh(request: RefreshTokenRequest): Result<RefreshTokenData> {
        return handleRequest { authApi.refreshToken(request) }
    }
}
