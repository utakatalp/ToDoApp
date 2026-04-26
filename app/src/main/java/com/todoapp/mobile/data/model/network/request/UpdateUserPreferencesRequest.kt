package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.Serializable

@Serializable
data class UpdateUserPreferencesRequest(
    val pushEnabled: Boolean,
)
