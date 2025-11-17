package com.swiftai.app.domain.usecase.chat

import com.swiftai.app.data.repository.ChatRepository
import javax.inject.Inject

class DeleteChatUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(chatId: String): Result<Unit> {
        return chatRepository.deleteChat(chatId)
    }
}
