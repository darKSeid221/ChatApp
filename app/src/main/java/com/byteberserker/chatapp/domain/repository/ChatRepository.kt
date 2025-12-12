package com.byteberserker.chatapp.domain.repository

import com.byteberserker.chatapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

interface ChatRepository {
    fun getAllMessages(): Flow<List<ChatMessage>>
    suspend fun sendMessage(text: String)
    suspend fun retryQueuedMessages()
}
