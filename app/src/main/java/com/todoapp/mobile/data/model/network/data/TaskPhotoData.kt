package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class TaskPhotoData(
    val id: Long,
    val url: String,
    val createdAt: Long,
)
