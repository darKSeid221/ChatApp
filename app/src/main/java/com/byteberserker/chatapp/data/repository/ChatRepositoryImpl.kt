package com.byteberserker.chatapp.data.repository

import com.byteberserker.chatapp.data.local.ChatDao
import com.byteberserker.chatapp.data.model.ChatMessageEntity
import com.byteberserker.chatapp.data.model.toDomain
import com.byteberserker.chatapp.data.model.toEntity
import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.model.MessageStatus
import com.byteberserker.chatapp.domain.repository.ChatRepository
import com.byteberserker.chatapp.data.remote.WebSocketClient
import com.byteberserker.chatapp.util.NetworkStatusTracker
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.count
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import okhttp3.Response
import okhttp3.WebSocket
import okhttp3.WebSocketListener
import com.byteberserker.chatapp.data.remote.model.NetworkChatMessage
import com.byteberserker.chatapp.data.remote.model.toEntity
import javax.inject.Inject

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val webSocketClient: WebSocketClient,
    private val networkStatusTracker: NetworkStatusTracker
) : ChatRepository {

    private val gson = Gson()
    private val socketUrl =
        "wss://s15551.blr1.piesocket.com/v3/1?api_key=khatcZwSouRtOCAanupkHtFfhxDAS9zCmh6pz61b&notify_self=1"

    init {
        observeNetworkStatus()
    }

    private fun observeNetworkStatus() {
        CoroutineScope(Dispatchers.IO).launch {
            networkStatusTracker.networkStatus.collect { isOnline ->
                if (isOnline) {
                    connectSocket()
                }
            }
        }
    }

    private fun connectSocket() {
        webSocketClient.connect(socketUrl, object : WebSocketListener() {
            override fun onOpen(webSocket: WebSocket, response: Response) {
                CoroutineScope(Dispatchers.IO).launch {
                    retryQueuedMessages()
                }
            }



            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    val networkMessage = gson.fromJson(text, NetworkChatMessage::class.java)
                    val entity = networkMessage.toEntity()
                    
                    if (entity != null) {
                        CoroutineScope(Dispatchers.IO).launch {
                            // Only insert if valid and not sent by me (to avoid duplication if logic requires)
                            if (!entity.isSentByMe) {
                                chatDao.insertMessage(entity.copy(status = MessageStatus.RECEIVED))
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        })
    }

    override fun getAllMessages(): Flow<List<ChatMessage>> {
        return chatDao.getAllMessages().map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(text: String) {
        val timestamp = System.currentTimeMillis()
        val initialMessage = ChatMessage(
            text = text,
            timestamp = timestamp,
            isSentByMe = true,
            status = MessageStatus.SENT
        )

        // Save to DB (get ID)
        val id = chatDao.insertMessage(initialMessage.toEntity())
        val messageWithId = initialMessage.copy(id = id)

        // Send
        val json = gson.toJson(messageWithId)
        val success = webSocketClient.sendMessage(json)

        if (!success) {
            chatDao.updateMessageStatus(id, MessageStatus.QUEUED)
        }
    }

    override suspend fun retryQueuedMessages() {
        val queued = chatDao.getMessagesByStatus(MessageStatus.QUEUED)
        queued.forEach { entity ->
            val domainMessage = entity.toDomain()
            val json = gson.toJson(domainMessage)
            if (webSocketClient.sendMessage(json)) {
                chatDao.updateMessageStatus(entity.id, MessageStatus.SENT)
            }
        }
    }
}

