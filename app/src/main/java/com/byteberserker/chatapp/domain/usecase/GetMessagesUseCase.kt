package com.byteberserker.chatapp.domain.usecase

import com.byteberserker.chatapp.domain.model.ChatMessage
import com.byteberserker.chatapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetMessagesUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<ChatMessage>> {
        return repository.getAllMessages()
    }
}
