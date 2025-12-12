package com.byteberserker.chatapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteberserker.chatapp.domain.model.Chat
import com.byteberserker.chatapp.domain.repository.ChatRepository
import com.byteberserker.chatapp.domain.usecase.GetChatsUseCase
import com.byteberserker.chatapp.util.NetworkStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ChatListViewModel @Inject constructor(
    private val getChatsUseCase: GetChatsUseCase,
    private val repository: ChatRepository,
    private val networkStatusTracker: NetworkStatusTracker
) : ViewModel() {

    val chats = getChatsUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val isOnline = networkStatusTracker.networkStatus.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true // Optimistic default
    )

    val errors = repository.connectionErrors

    init {
        seedData()
    }

    private fun seedData() {
        viewModelScope.launch {
            // Only seed Global Chat if it's missing (First Run)
            // Do NOT clear chats here, or we lose DMs on restart/recreation
            
            // Check if Global Chat exists
            val existingChats = repository.getChats().firstOrNull()
            val hasGlobalChat = existingChats?.any { it.id == 1L } == true
            
            if (!hasGlobalChat) {
                // Restore persistent Global Chat so the user has an entry point
                val globalChat = Chat(
                    id = 1L, 
                    name = "Global Chat", 
                    lastMessage = "Welcome to the chat!", 
                    lastMessageTime = System.currentTimeMillis(), 
                    unreadCount = 0,
                    type = "GLOBAL"
                )
                repository.createChat(globalChat)
            }
        }
    }

    fun deleteChat(chatId: Long) {
        viewModelScope.launch {
            repository.deleteChat(chatId)
        }
    }
}
