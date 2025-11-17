package com.swiftai.app.data.repository

import com.swiftai.app.data.remote.api.SwiftAIApi
import com.swiftai.app.data.remote.firebase.FirestoreService
import com.swiftai.app.domain.model.Chat
import com.swiftai.app.domain.model.Message
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestoreService: FirestoreService,
    private val swiftAIApi: SwiftAIApi
) {

    suspend fun createChat(chat: Chat): Result<String> {
        return firestoreService.createChat(chat)
    }

    fun getChatsFlow(userId: String): Flow<List<Chat>> {
        return firestoreService.getChatsFlow(userId)
    }

    suspend fun updateChat(chat: Chat): Result<Unit> {
        return firestoreService.updateChat(chat)
    }

    suspend fun deleteChat(chatId: String): Result<Unit> {
        return firestoreService.deleteChat(chatId)
    }

    suspend fun sendMessage(message: Message): Result<String> {
        return firestoreService.sendMessage(message)
    }

    fun getMessagesFlow(chatId: String): Flow<List<Message>> {
        return firestoreService.getMessagesFlow(chatId)
    }

    suspend fun getAIResponse(
        prompt: String,
        model: String,
        maxLength: Int
    ): Result<String> {
        val result = swiftAIApi.sendMessage(prompt, model, maxLength)
        return if (result.isSuccess) {
            val response = result.getOrThrow()
            if (response.success) {
                Result.success(response.response)
            } else {
                Result.failure(Exception(response.error ?: "Unknown error"))
            }
        } else {
            result.map { "" }
        }
    }

    suspend fun generateChatTitle(firstMessage: String): String {
        return try {
            val result = swiftAIApi.sendMessage(
                "Generate a short 3-5 word title for a chat that starts with: \"$firstMessage\". Only respond with the title, nothing else.",
                "swiftai-mini",
                20
            )

            if (result.isSuccess) {
                val response = result.getOrThrow()
                if (response.success && response.response.isNotEmpty()) {
                    response.response.trim()
                } else {
                    firstMessage.take(30)
                }
            } else {
                firstMessage.take(30)
            }
        } catch (e: Exception) {
            firstMessage.take(30)
        }
    }
}
