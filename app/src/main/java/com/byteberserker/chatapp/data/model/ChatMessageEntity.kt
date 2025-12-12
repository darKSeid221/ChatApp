package com.byteberserker.chatapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.model.MessageStatus

@Entity(tableName = "messages", foreignKeys = [
    androidx.room.ForeignKey(
        entity = ChatEntity::class,
        parentColumns = ["id"],
        childColumns = ["chatId"],
        onDelete = androidx.room.ForeignKey.CASCADE
    )
])
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val chatId: Long,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val status: MessageStatus = MessageStatus.SENT,
    val senderId: Long? = null,
    val senderName: String? = null
)

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        text = text,
        timestamp = timestamp,
        isSentByMe = isSentByMe,
        status = status,
        senderId = senderId,
        senderName = senderName
    )
}

fun ChatMessage.toEntity(chatId: Long): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        chatId = chatId,
        text = text,
        timestamp = timestamp,
        isSentByMe = isSentByMe,
        status = status,
        senderId = senderId,
        senderName = senderName
    )
}
