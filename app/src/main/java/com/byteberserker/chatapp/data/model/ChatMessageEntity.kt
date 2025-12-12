package com.byteberserker.chatapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.model.MessageStatus

@Entity(tableName = "messages")
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val text: String,
    val timestamp: Long,
    val isSentByMe: Boolean,
    val status: MessageStatus = MessageStatus.SENT
)

fun ChatMessageEntity.toDomain(): ChatMessage {
    return ChatMessage(
        id = id,
        text = text,
        timestamp = timestamp,
        isSentByMe = isSentByMe,
        status = status
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        text = text,
        timestamp = timestamp,
        isSentByMe = isSentByMe,
        status = status
    )
}
