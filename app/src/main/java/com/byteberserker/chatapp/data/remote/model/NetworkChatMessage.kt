package com.byteberserker.chatapp.data.remote.model

import com.byteberserker.chatapp.data.model.ChatMessageEntity
import com.byteberserker.chatapp.domain.model.MessageStatus

data class NetworkChatMessage(
    val id: Long? = null,
    val text: String? = null,
    val timestamp: Long? = null,
    val isSentByMe: Boolean? = null, 
    val status: String? = null,
    val senderId: Long? = null,
    val senderName: String? = null,
    val chatId: Long? = null
)

fun NetworkChatMessage.toEntity(chatId: Long = 1): ChatMessageEntity? {
    if (text == null) return null // Skip messages without text
    // Default values for missing fields to avoid crashes
    return ChatMessageEntity(
        id = 0, // Force auto-generate to avoid collisions with server IDs
        chatId = chatId,
        text = text,
        timestamp = if ((timestamp ?: 0L) > 1577836800000L) timestamp!! else System.currentTimeMillis(), // Verify if > Year 2020, else use Now
        isSentByMe = isSentByMe ?: false,
        status = MessageStatus.RECEIVED, // Default to RECEIVED for incoming
        senderId = senderId,
        senderName = senderName
    )
}
