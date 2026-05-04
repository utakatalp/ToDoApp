package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class GroupTaskRequest(
    val title: String,
    val description: String?,
    val dueDate: Long,
    val isCompleted: Boolean,
    @SerialName("priority") val priority: String? = null,
    val assigneeId: Long? = null,
    val locationLat: Double? = null,
    val locationLng: Double? = null,
    val locationName: String? = null,
    val locationAddress: String? = null,
)
