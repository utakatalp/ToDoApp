package com.todoapp.mobile.data.model.network.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChatMessageResponseData(
    @SerialName("text") val text: String,
    @SerialName("meta") val meta: ChatTurnMeta,
)

@Serializable
data class ChatTurnMeta(
    @SerialName("roundTrips") val roundTrips: Int,
    @SerialName("refused") val refused: Boolean,
    @SerialName("serverMs") val serverMs: Long,
)
