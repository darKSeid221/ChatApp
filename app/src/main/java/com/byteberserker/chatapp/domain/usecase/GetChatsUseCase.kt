package com.byteberserker.chatapp.domain.usecase

import com.byteberserker.chatapp.domain.model.Chat
import com.byteberserker.chatapp.domain.repository.ChatRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    operator fun invoke(): Flow<List<Chat>> {
        return repository.getChats()
    }
}
