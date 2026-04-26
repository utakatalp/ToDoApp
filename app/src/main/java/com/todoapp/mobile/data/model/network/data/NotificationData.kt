package com.todoapp.mobile.data.model.network.data

import kotlinx.serialization.Serializable

@Serializable
data class NotificationData(
    val id: Long,
    val type: String,
    val title: String,
    val body: String,
    val payload: Map<String, String> = emptyMap(),
    val isRead: Boolean,
    val createdAt: Long,
)

@Serializable
data class NotificationListData(
    val items: List<NotificationData>,
    val hasMore: Boolean,
)

@Serializable
data class UnreadCountData(val count: Long)
