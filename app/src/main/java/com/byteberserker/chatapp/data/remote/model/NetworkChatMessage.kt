package com.byteberserker.chatapp.data.remote.model

import com.byteberserker.chatapp.data.model.ChatMessageEntity
import com.byteberserker.chatapp.domain.model.MessageStatus

data class NetworkChatMessage(
    val id: Long? = null,
    val text: String? = null,
    val timestamp: Long? = null,
    val isSentByMe: Boolean? = null, 
    val status: String? = null // status might come as string or enum
)

fun NetworkChatMessage.toEntity(): ChatMessageEntity? {
    if (text == null) return null // Skip messages without text
    // Default values for missing fields to avoid crashes
    return ChatMessageEntity(
        id = id ?: 0,
        text = text,
        timestamp = timestamp ?: System.currentTimeMillis(),
        isSentByMe = isSentByMe ?: false,
        status = MessageStatus.RECEIVED // Default to RECEIVED for incoming
    )
}
