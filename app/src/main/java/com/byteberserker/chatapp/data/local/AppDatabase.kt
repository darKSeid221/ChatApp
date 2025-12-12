package com.byteberserker.chatapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.byteberserker.chatapp.data.model.ChatEntity
import com.byteberserker.chatapp.data.model.ChatMessageEntity

@Database(entities = [ChatMessageEntity::class, ChatEntity::class], version = 2)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun chatDao(): ChatDao
}
