package com.todoapp.mobile.domain.repository

import com.todoapp.mobile.data.model.network.request.ChatHistoryTurn
import com.todoapp.mobile.data.model.network.response.ChatMessageResponseData
import com.todoapp.mobile.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun observeMessages(): Flow<List<ChatMessage>>

    suspend fun getMessages(): List<ChatMessage>

    suspend fun appendUserMessage(content: String): Long

    suspend fun appendAssistantMessage(content: String): Long

    suspend fun clear()

    /**
     * Sends a chat message to the server-side DoneBot proxy. The server runs
     * function calling against Vertex AI on its end; the client only deals
     * with prompt/history in/text out.
     */
    suspend fun sendMessage(
        prompt: String,
        locale: String,
        history: List<ChatHistoryTurn>,
    ): Result<ChatMessageResponseData>
}
