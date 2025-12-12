package com.byteberserker.chatapp.domain.model

data class Chat(
    val id: Long,
    val name: String,
    val lastMessage: String,
    val lastMessageTime: Long,
    val unreadCount: Int,
    val type: String
)
