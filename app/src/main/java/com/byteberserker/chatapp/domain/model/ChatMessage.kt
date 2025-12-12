package com.byteberserker.chatapp.domain.model

enum class MessageStatus {
    SENT, QUEUED, FAILED, RECEIVED
}

data class ChatMessage(
    val id: Long = 0,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val status: MessageStatus = MessageStatus.SENT
)
