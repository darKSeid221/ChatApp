package com.byteberserker.chatapp.ui.chat

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.byteberserker.chatapp.domain.usecase.GetMessagesUseCase
import com.byteberserker.chatapp.domain.usecase.SendMessageUseCase
import com.byteberserker.chatapp.util.NetworkStatusTracker
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

import androidx.lifecycle.SavedStateHandle

import com.byteberserker.chatapp.domain.repository.ChatRepository

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    private val repository: ChatRepository,
    val networkStatusTracker: NetworkStatusTracker,
    savedStateHandle: SavedStateHandle
) : ViewModel() {
    
    private val chatId: Long = checkNotNull(savedStateHandle["chatId"])

    val chat = repository.getChat(chatId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        null
    )

    val messages = getMessagesUseCase(chatId).stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )

    val isOnline = networkStatusTracker.networkStatus.stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        true
    )

    val errors = repository.connectionErrors

    init {
        // Mark as read when entering the chat
        markAsRead()
    }

    fun markAsRead() {
        viewModelScope.launch {
            repository.markChatAsRead(chatId)
        }
    }

    fun sendMessage(text: String) {
        viewModelScope.launch {
            sendMessageUseCase(chatId, text)
        }
    }

    fun onResume() {
        repository.setActiveChat(chatId)
    }

    fun onPause() {
        repository.setActiveChat(null)
    }
}
