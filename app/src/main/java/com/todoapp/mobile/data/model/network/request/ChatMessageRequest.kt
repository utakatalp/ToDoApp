package com.todoapp.mobile.data.model.network.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Body for `POST /chat/message`. The server is stateless; the client resends history each turn. */
@Serializable
data class ChatMessageRequest(
    @SerialName("prompt") val prompt: String,
    /** ISO 639-1 language code: "en" or "tr". Server falls back to "en" when missing. */
    @SerialName("locale") val locale: String,
    @SerialName("history") val history: List<ChatHistoryTurn> = emptyList(),
)

@Serializable
data class ChatHistoryTurn(
    /** "user" or "assistant" */
    @SerialName("role") val role: String,
    @SerialName("content") val content: String,
)
