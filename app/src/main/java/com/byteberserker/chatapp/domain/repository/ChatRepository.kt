package com.byteberserker.chatapp.domain.repository

import com.byteberserker.chatapp.domain.model.ChatMessage
import kotlinx.coroutines.flow.Flow

import com.byteberserker.chatapp.domain.model.Chat

interface ChatRepository {
    fun getChats(): Flow<List<Chat>>
    fun getChat(chatId: Long): Flow<Chat?>
    fun getMessages(chatId: Long): Flow<List<ChatMessage>>
    suspend fun sendMessage(chatId: Long, text: String)
    suspend fun retryQueuedMessages()
    suspend fun createChat(chat: Chat)
    suspend fun clearChats()
    suspend fun markChatAsRead(chatId: Long)
    
    fun setActiveChat(chatId: Long?)
    val connectionErrors: Flow<String>
}
