package com.byteberserker.chatapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.byteberserker.chatapp.data.model.ChatEntity
import com.byteberserker.chatapp.data.model.ChatMessageEntity
import com.byteberserker.chatapp.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM chats")
    fun getChats(): Flow<List<ChatEntity>>

    @Query("SELECT * FROM messages WHERE chatId = :chatId ORDER BY timestamp DESC")
    fun getMessages(chatId: Long): Flow<List<ChatMessageEntity>>

    @Query("SELECT EXISTS(SELECT 1 FROM chats WHERE id = :id)")
    suspend fun chatExists(id: Long): Boolean

    @Query("SELECT * FROM chats WHERE id = :id")
    fun getChat(id: Long): Flow<ChatEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertChat(chat: ChatEntity)
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM messages WHERE status = :status")
    suspend fun getMessagesByStatus(status: MessageStatus): List<ChatMessageEntity>

    @Query("UPDATE messages SET status = :newStatus WHERE id = :id")
    suspend fun updateMessageStatus(id: Long, newStatus: MessageStatus)
    
    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()

    @Query("DELETE FROM chats")
    suspend fun clearAllChats()
    
    @Query("UPDATE chats SET lastMessage = :lastMessage, lastMessageTime = :time, unreadCount = unreadCount + :unreadIncrement WHERE id = :chatId")
    suspend fun updateLastMessage(chatId: Long, lastMessage: String, time: Long, unreadIncrement: Int)

    @Query("UPDATE chats SET unreadCount = 0 WHERE id = :chatId")
    suspend fun resetUnreadCount(chatId: Long)
}
