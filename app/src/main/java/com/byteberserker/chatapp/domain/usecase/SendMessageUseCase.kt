package com.byteberserker.chatapp.domain.usecase

import com.byteberserker.chatapp.domain.repository.ChatRepository
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val repository: ChatRepository
) {
    suspend operator fun invoke(text: String) {
        repository.sendMessage(text)
    }
}
