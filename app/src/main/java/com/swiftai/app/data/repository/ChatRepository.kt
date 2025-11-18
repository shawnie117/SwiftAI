package com.swiftai.app.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swiftai.app.data.remote.api.GeminiApi
import com.swiftai.app.data.remote.api.SwiftAIApi
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
    private val geminiApi: GeminiApi,
    private val swiftAIApi: SwiftAIApi
) {
    fun getChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        Log.d("ChatRepository", "Setting up getChatsFlow for userId: $userId")

        val listener = firestore.collection("chats")
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.e("ChatRepository", "Error in getChatsFlow: ${error.message}", error)
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                if (snapshot == null || snapshot.isEmpty) {
                    Log.d("ChatRepository", "Snapshot is null or empty")
                    trySend(emptyList())
                    return@addSnapshotListener
                }

                val chats = snapshot.documents.mapNotNull { doc ->
                    try {
                        Chat(
                            id = doc.getString("id") ?: doc.id,
                            userId = doc.getString("userId") ?: "",
                            title = doc.getString("title") ?: "New Chat",
                            createdAt = doc.getLong("createdAt") ?: System.currentTimeMillis(),
                            updatedAt = doc.getLong("updatedAt") ?: System.currentTimeMillis(),
                            lastMessageTime = doc.getLong("lastMessageTime") ?: System.currentTimeMillis(),
                            model = doc.getString("model") ?: "gemini-pro",
                            messageCount = doc.getLong("messageCount")?.toInt() ?: 0
                        ).also {
                            Log.d("ChatRepository", "Loaded chat: id=${it.id}, title=${it.title}, userId=${it.userId}")
                        }
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "Error parsing chat document ${doc.id}: ${e.message}", e)
                        null
                    }
                }

                val sortedChats = chats.sortedByDescending { it.lastMessageTime }
                Log.d("ChatRepository", "Sending ${sortedChats.size} chats to Flow")
                trySend(sortedChats)
            }
        awaitClose {
            Log.d("ChatRepository", "Closing getChatsFlow listener")
            listener.remove()
        }
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
                val messages = snapshot?.documents?.mapNotNull { doc ->
                    try {
                        Message(
                            id = doc.getString("id") ?: "",
                            chatId = doc.getString("chatId") ?: "",
                            content = doc.getString("content") ?: "",
                            isUser = doc.getBoolean("isUser") ?: false,
                            timestamp = doc.getLong("timestamp") ?: 0L
                        )
                    } catch (e: Exception) {
                        Log.e("ChatRepository", "Error parsing message: ${e.message}")
                        null
                    }
                } ?: emptyList()

                Log.d("ChatRepository", "Loaded ${messages.size} messages")
                messages.forEach { msg ->
                    Log.d("ChatRepository", "Message: isUser=${msg.isUser}, content=${msg.content.take(30)}")
                }

                trySend(messages)
            }
        awaitClose { listener.remove() }
    }

    suspend fun createChat(chat: Chat) {
        Log.d("ChatRepository", "Creating chat: ${chat.id} for user: ${chat.userId}")

        val chatData = hashMapOf(
            "id" to chat.id,
            "userId" to chat.userId,
            "title" to chat.title,
            "createdAt" to chat.createdAt,
            "updatedAt" to chat.updatedAt,
            "lastMessageTime" to chat.lastMessageTime,
            "model" to chat.model,
            "messageCount" to chat.messageCount
        )

        firestore.collection("chats")
            .document(chat.id)
            .set(chatData)
            .await()

        Log.d("ChatRepository", "Chat created successfully: ${chat.id}")
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

            val messageData = hashMapOf(
                "id" to messageWithId.id,
                "chatId" to messageWithId.chatId,
                "content" to messageWithId.content,
                "isUser" to messageWithId.isUser,
                "timestamp" to messageWithId.timestamp
            )

            firestore.collection("messages")
                .document(messageWithId.id)
                .set(messageData)
                .await()

            Log.d("ChatRepository", "User message saved to Firestore with isUser=${messageWithId.isUser}")

            val chatDoc = firestore.collection("chats").document(messageWithId.chatId).get().await()
            val currentTitle = chatDoc.getString("title") ?: "New Chat"

            val newTitle = if (currentTitle == "New Chat" && messageWithId.isUser) {
                messageWithId.content.take(50).trim()
            } else {
                currentTitle
            }

            firestore.collection("chats")
                .document(messageWithId.chatId)
                .update(
                    mapOf(
                        "lastMessageTime" to messageWithId.timestamp,
                        "title" to newTitle
                    )
                )
                .await()

            Log.d("ChatRepository", "Calling Gemini API...")

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

        val aiMessageData = hashMapOf(
            "id" to aiMessage.id,
            "chatId" to aiMessage.chatId,
            "content" to aiMessage.content,
            "isUser" to aiMessage.isUser,
            "timestamp" to aiMessage.timestamp
        )

        firestore.collection("messages")
            .document(aiMessage.id)
            .set(aiMessageData)
            .await()

        Log.d("ChatRepository", "AI response saved to Firestore with isUser=${aiMessage.isUser}")

        firestore.collection("chats")
            .document(chatId)
            .update("lastMessageTime", aiMessage.timestamp)
            .await()

        Log.d("ChatRepository", "AI response saved to Firestore")

        return aiMessage
    }

    // New: Send message and get full history from backend
    suspend fun sendMessageWithHistory(prompt: String, model: String, chatId: String? = null): Result<List<Message>> {
        return try {
            val result = swiftAIApi.sendMessage(prompt, model, 100, chatId)
            if (result.isSuccess) {
                val response = result.getOrNull()
                if (response != null) {
                    // Save messages to Firestore
                    response.history.forEach { msg ->
                        // Message objects already have the correct structure
                        saveMessageToFirestore(msg)
                    }
                    Result.success(response.history)
                } else {
                    Result.failure(Exception("Empty response from backend"))
                }
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error in sendMessageWithHistory: ${e.message}", e)
            Result.failure(e)
        }
    }

    // New: Get chat history from backend
    suspend fun getHistoryFromBackend(chatId: String): Result<List<Message>> {
        return try {
            val result = swiftAIApi.getHistory(chatId)
            if (result.isSuccess) {
                val chatResponses = result.getOrNull()
                if (chatResponses != null && chatResponses.isNotEmpty()) {
                    // Extract all messages from all chat responses and flatten
                    val allMessages = chatResponses.flatMap { it.history }

                    // Save messages to Firestore
                    allMessages.forEach { msg ->
                        saveMessageToFirestore(msg)
                    }
                    Result.success(allMessages)
                } else {
                    Result.failure(Exception("Empty history from backend"))
                }
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Unknown error"))
            }
        } catch (e: Exception) {
            Log.e("ChatRepository", "Error in getHistoryFromBackend: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Helper: Save message to Firestore
    private suspend fun saveMessageToFirestore(message: Message) {
        val messageData = hashMapOf(
            "id" to message.id,
            "chatId" to message.chatId,
            "content" to message.content,
            "isUser" to message.isUser,
            "timestamp" to message.timestamp
        )
        firestore.collection("messages")
            .document(message.id)
            .set(messageData)
            .await()
    }
}
