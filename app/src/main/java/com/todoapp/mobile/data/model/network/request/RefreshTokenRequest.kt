package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenRequest(@SerialName("refreshToken") val refreshToken: String)
