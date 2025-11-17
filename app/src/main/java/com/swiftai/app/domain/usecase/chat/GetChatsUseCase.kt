package com.swiftai.app.domain.usecase.chat

import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.domain.model.Chat
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetChatsUseCase @Inject constructor(
    private val chatRepository: ChatRepository
) {
    operator fun invoke(userId: String): Flow<List<Chat>> {
        return chatRepository.getChatsFlow(userId)
    }
}
