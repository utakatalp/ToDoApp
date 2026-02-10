package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class FacebookLoginRequest(@SerialName("token") val token: String)
