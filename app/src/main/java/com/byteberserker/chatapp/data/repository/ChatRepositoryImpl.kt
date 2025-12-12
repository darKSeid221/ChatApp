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
import com.byteberserker.chatapp.data.local.UserSession
import com.byteberserker.chatapp.domain.model.Chat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow

class ChatRepositoryImpl @Inject constructor(
    private val chatDao: ChatDao,
    private val webSocketClient: WebSocketClient,
    private val networkStatusTracker: NetworkStatusTracker,
    private val userSession: UserSession
) : ChatRepository {

    private val gson = Gson()
    private var activeChatId: Long? = null

    override fun setActiveChat(chatId: Long?) {
        activeChatId = chatId
    }

    private val socketUrl =
        "wss://s15551.blr1.piesocket.com/v3/1?api_key=khatcZwSouRtOCAanupkHtFfhxDAS9zCmh6pz61b&notify_self=1"

    private val _connectionErrors = Channel<String>(Channel.BUFFERED)
    override val connectionErrors = _connectionErrors.receiveAsFlow()

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
            
            override fun onFailure(webSocket: WebSocket, t: Throwable, response: Response?) {
                CoroutineScope(Dispatchers.IO).launch {
                    _connectionErrors.send("Connection failed: ${t.message}")
                }
            }

            override fun onMessage(webSocket: WebSocket, text: String) {
                try {
                    android.util.Log.d("ChatRepo", "Received: $text")
                    val networkMessage = gson.fromJson(text, NetworkChatMessage::class.java)
                    
                    // Determine Chat Type and ID
                    val receivedChatId = networkMessage.chatId ?: networkMessage.senderId ?: 1L
                    val isGlobal = receivedChatId == 1L
                    val finalChatId = receivedChatId
                    
                    val chatName = if (isGlobal) "Global Chat" else (networkMessage.senderName ?: "Unknown")
                    val chatType = if (isGlobal) "GLOBAL" else "DM"

                    CoroutineScope(Dispatchers.IO).launch {
                         // Update or Create Chat
                        if (networkMessage.isSentByMe != true) {
                            val exists = chatDao.chatExists(finalChatId)
                            val increment = if (finalChatId == activeChatId) 0 else 1
                            
                            if (exists) {
                                chatDao.updateLastMessage(
                                    chatId = finalChatId,
                                    lastMessage = networkMessage.text ?: "",
                                    time = networkMessage.timestamp ?: System.currentTimeMillis(),
                                    unreadIncrement = increment
                                )
                            } else {
                                 val chatEntity = com.byteberserker.chatapp.data.model.ChatEntity(
                                    id = finalChatId,
                                    name = chatName,
                                    lastMessage = networkMessage.text ?: "",
                                    lastMessageTime = networkMessage.timestamp ?: System.currentTimeMillis(),
                                    unreadCount = increment,
                                    type = chatType
                                )
                                chatDao.insertChat(chatEntity)
                            }
                        }

                        // Insert Message (Force Auto-ID via toEntity mapping)
                        val entity = networkMessage.toEntity(chatId = finalChatId)
                        if (entity != null) {
                            if (networkMessage.isSentByMe != true) {
                                chatDao.insertMessage(entity.copy(status = MessageStatus.RECEIVED))
                            }
                        }
                    }
                } catch (e: Exception) {
                    android.util.Log.e("ChatRepo", "JSON Parsing or DB Error", e)
                    CoroutineScope(Dispatchers.IO).launch {
                        _connectionErrors.send("Error processing message: ${e.message}")
                    }
                    e.printStackTrace()
                }
            }
        })
    }

    override fun getChats(): Flow<List<Chat>> {
         return chatDao.getChats().map { entities ->
             entities.map { entity ->
                 Chat(
                     id = entity.id,
                     name = entity.name,
                     lastMessage = entity.lastMessage,
                     lastMessageTime = entity.lastMessageTime,
                     unreadCount = entity.unreadCount,
                     type = entity.type
                 )
             }
         }
    }

    override fun getChat(chatId: Long): Flow<Chat?> {
        return chatDao.getChat(chatId).map { entity ->
            entity?.let {
                Chat(
                    id = it.id,
                    name = it.name,
                    lastMessage = it.lastMessage,
                    lastMessageTime = it.lastMessageTime,
                    unreadCount = it.unreadCount,
                    type = it.type
                )
            }
        }
    }

    override fun getMessages(chatId: Long): Flow<List<ChatMessage>> {
        return chatDao.getMessages(chatId).map { list ->
            list.map { it.toDomain() }
        }
    }

    override suspend fun sendMessage(chatId: Long, text: String) {
        val timestamp = System.currentTimeMillis()
        val initialMessage = ChatMessage(
            text = text,
            timestamp = timestamp,
            isSentByMe = true,
            status = MessageStatus.SENT,
            senderId = userSession.myUserId,
            senderName = userSession.myUserName
        )

        // Save to DB (get ID)
        val id = chatDao.insertMessage(initialMessage.toEntity(chatId))
        val messageWithId = initialMessage.copy(id = id)

        // Wrapper for Network Payload
        // We reuse NetworkChatMessage for sending to simplicity, or create a specific payload DTO.
        // Assuming server accepts NetworkChatMessage structure.
        val networkPayload = NetworkChatMessage(
            id = id,
            text = text,
            timestamp = timestamp,
            isSentByMe = true, // Server might ignore this
            status = "SENT",
             senderId = userSession.myUserId,
            senderName = userSession.myUserName,
            chatId = chatId // Tell server where this goes
        )

        val json = gson.toJson(networkPayload)
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

    override suspend fun createChat(chat: Chat) {
        val entity = com.byteberserker.chatapp.data.model.ChatEntity(
            id = chat.id,
            name = chat.name,
            lastMessage = chat.lastMessage,
            lastMessageTime = chat.lastMessageTime,
            unreadCount = chat.unreadCount,
            type = chat.type
        )
        chatDao.insertChat(entity)
    }

    override suspend fun clearChats() {
        chatDao.clearAllChats()
    }

    override suspend fun markChatAsRead(chatId: Long) {
        chatDao.resetUnreadCount(chatId)
    }
}

