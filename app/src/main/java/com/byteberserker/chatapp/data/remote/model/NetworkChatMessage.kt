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
    if (text == null) return null
    return ChatMessageEntity(
        id = 0,
        chatId = chatId,
        text = text,
        timestamp = if ((timestamp ?: 0L) > 1577836800000L) timestamp!! else System.currentTimeMillis(), // Verify if > Year 2020, else use Now
        isSentByMe = isSentByMe ?: false,
        status = MessageStatus.RECEIVED,
        senderId = senderId,
        senderName = senderName
    )
}
