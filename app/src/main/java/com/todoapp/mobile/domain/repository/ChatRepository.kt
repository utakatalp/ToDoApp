package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(): Flow<List<ChatMessage>>

    suspend fun getMessages(): List<ChatMessage>

    suspend fun appendUserMessage(content: String): Long

    suspend fun appendAssistantMessage(content: String): Long

    suspend fun clear()
}
