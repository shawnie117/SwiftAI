package com.swiftai.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swiftai.app.data.remote.api.GeminiApi
import com.swiftai.app.domain.model.Chat
import com.swiftai.app.domain.model.Message
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ChatRepository @Inject constructor(
    private val firestore: FirebaseFirestore,
    private val geminiApi: GeminiApi
) {
    fun getChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        val listener = firestore.collection("chats")
            .whereEqualTo("userId", userId)
            .orderBy("lastMessageTime", Query.Direction.DESCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val chats = snapshot?.documents?.mapNotNull {
                    it.toObject(Chat::class.java)
                } ?: emptyList()
                trySend(chats)
            }
        awaitClose { listener.remove() }
    }

    fun getMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        val listener = firestore.collection("messages")
            .whereEqualTo("chatId", chatId)
            .orderBy("timestamp", Query.Direction.ASCENDING)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                val messages = snapshot?.documents?.mapNotNull {
                    it.toObject(Message::class.java)
                } ?: emptyList()
                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createChat(chat: Chat) {
        firestore.collection("chats")
            .document(chat.id)
            .set(chat)
            .await()
    }

    suspend fun deleteChat(chatId: String) {
        val messagesSnapshot = firestore.collection("messages")
            .whereEqualTo("chatId", chatId)
            .get()
            .await()
        messagesSnapshot.documents.forEach { it.reference.delete() }
        firestore.collection("chats")
            .document(chatId)
            .delete()
            .await()
    }

    suspend fun sendMessage(message: Message): Result<String> {
        return try {
            Log.d("ChatRepository", "Sending message: ${message.content}")

            val messageWithId = if (message.id.isBlank()) {
                message.copy(id = UUID.randomUUID().toString())
            } else {
                message
            }

            // Save user message
            firestore.collection("messages")
                .document(messageWithId.id)
                .set(messageWithId)
                .await()

            Log.d("ChatRepository", "User message saved to Firestore")

            // Update chat title
            firestore.collection("chats")
                .document(messageWithId.chatId)
                .update(
                    mapOf(
                        "lastMessageTime" to messageWithId.timestamp,
                        "title" to messageWithId.content.take(50)
                    )
                )
                .await()

            Log.d("ChatRepository", "Calling Gemini API...")

            // Get AI response
            val geminiResult = geminiApi.sendMessage(messageWithId.content)

            Log.d("ChatRepository", "Gemini result: ${geminiResult.isSuccess}")

            return when {
                geminiResult.isSuccess -> {
                    val response = geminiResult.getOrNull()
                    if (!response.isNullOrBlank()) {
                        Log.d("ChatRepository", "Got valid response: $response")
                        saveAIResponse(messageWithId.chatId, response)
                        Result.success(response)
                    } else {
                        Log.e("ChatRepository", "Response was null or blank")
                        Result.failure(Exception("Empty response from AI"))
                    }
                }
                else -> {
                    val error = geminiResult.exceptionOrNull()
                    Log.e("ChatRepository", "Gemini error: ${error?.message}")
                    Result.failure(error ?: Exception("Unknown error"))
                }
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error in sendMessage: ${e.message}", e)
            Result.failure(e)
        }
    }

    suspend fun saveAIResponse(chatId: String, content: String): Message {
        val aiMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            content = content,
            isUser = false,
            timestamp = System.currentTimeMillis()
        )

        Log.d("ChatRepository", "Saving AI response: $content")

        firestore.collection("messages")
            .document(aiMessage.id)
            .set(aiMessage)
            .await()

        firestore.collection("chats")
            .document(chatId)
            .update("lastMessageTime", aiMessage.timestamp)
            .await()

        Log.d("ChatRepository", "AI response saved to Firestore")

        return aiMessage
    }
}
