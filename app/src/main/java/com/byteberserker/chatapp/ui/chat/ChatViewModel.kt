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

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val getMessagesUseCase: GetMessagesUseCase,
    private val sendMessageUseCase: SendMessageUseCase,
    val networkStatusTracker: NetworkStatusTracker
) : ViewModel() {
    
    val messages = getMessagesUseCase().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )


    fun sendMessage(text: String) {
        viewModelScope.launch {
            sendMessageUseCase(text)
        }
    }
}
