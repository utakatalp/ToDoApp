package com.todoapp.mobile.domain.model

data class ChatMessage(
    val id: Long,
    val role: Role,
    val content: String,
    val createdAt: Long,
) {
    enum class Role { USER, ASSISTANT }
}
