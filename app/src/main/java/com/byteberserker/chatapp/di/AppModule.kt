package com.byteberserker.chatapp.di

import android.content.Context
import androidx.room.Room
import com.byteberserker.chatapp.data.local.AppDatabase
import com.byteberserker.chatapp.data.local.ChatDao
import com.byteberserker.chatapp.data.remote.WebSocketClient
import com.byteberserker.chatapp.data.repository.ChatRepositoryImpl
import com.byteberserker.chatapp.domain.repository.ChatRepository
import com.byteberserker.chatapp.util.NetworkStatusTracker
import dagger.Binds
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {
    @Binds
    @Singleton
    abstract fun bindChatRepository(
        chatRepositoryImpl: ChatRepositoryImpl
    ): ChatRepository
}

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "chat_database"
        ).build()
    }

    @Provides
    @Singleton
    fun provideChatDao(appDatabase: AppDatabase): ChatDao {
        return appDatabase.chatDao()
    }

    @Provides
    @Singleton
    fun provideWebSocketClient(): WebSocketClient {
        return WebSocketClient()
    }

    @Provides
    @Singleton
    fun provideNetworkStatusTracker(@ApplicationContext context: Context): NetworkStatusTracker {
        return NetworkStatusTracker(context)
    }
}
