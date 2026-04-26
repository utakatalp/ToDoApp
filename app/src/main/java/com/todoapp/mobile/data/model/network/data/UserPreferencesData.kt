package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class UserPreferencesData(
    val pushEnabled: Boolean,
)
