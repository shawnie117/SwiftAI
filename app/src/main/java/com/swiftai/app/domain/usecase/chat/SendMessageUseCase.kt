package com.swiftai.app.domain.usecase.chat

import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.domain.model.Message
import javax.inject.Inject

class SendMessageUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    suspend operator fun invoke(message: Message): Result<String> {
        return chatRepository.sendMessage(message)
    }

    suspend fun getAIResponse(prompt: String, model: String, maxLength: Int): Result<String> {
        return chatRepository.getAIResponse(prompt, model, maxLength)
    }
}
