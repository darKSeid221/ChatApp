package com.byteberserker.chatapp.data.local

import androidx.room.TypeConverter
import com.byteberserker.chatapp.domain.model.MessageStatus

class Converters {
    @TypeConverter
    fun fromStatus(value: MessageStatus): String = value.name

    @TypeConverter
    fun toStatus(value: String): MessageStatus = MessageStatus.valueOf(value)
}
