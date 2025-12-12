package com.byteberserker.chatapp.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.byteberserker.chatapp.data.model.ChatMessageEntity
import com.byteberserker.chatapp.domain.model.MessageStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface ChatDao {
    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun getAllMessages(): Flow<List<ChatMessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertMessage(message: ChatMessageEntity): Long

    @Query("SELECT * FROM messages WHERE status = :status")
    suspend fun getMessagesByStatus(status: MessageStatus): List<ChatMessageEntity>

    @Query("UPDATE messages SET status = :newStatus WHERE id = :id")
    suspend fun updateMessageStatus(id: Long, newStatus: MessageStatus)
    
    @Query("DELETE FROM messages")
    suspend fun clearAllMessages()
}
