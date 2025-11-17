package com.swiftai.app.data.remote.firebase

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.swiftai.app.domain.model.Chat
import com.swiftai.app.domain.model.Message
import com.swiftai.app.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirestoreService @Inject constructor() {
    private val firestore = FirebaseFirestore.getInstance()

    // Collections
    private val usersCollection = firestore.collection("users")
    private val chatsCollection = firestore.collection("chats")
    private val messagesCollection = firestore.collection("messages")

    // ===== USER OPERATIONS =====

    suspend fun createUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error creating user: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun getUser(userId: String): Result<User?> {
        return try {
            val document = usersCollection.document(userId).get().await()
            val user = document.toObject(User::class.java)
            Result.success(user)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error getting user: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return try {
            usersCollection.document(user.uid).set(user).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating user: ${e.message}")
            Result.failure(e)
        }
    }

    // ===== CHAT OPERATIONS =====

    suspend fun createChat(chat: Chat): Result<String> {
        return try {
            val docRef = if (chat.id.isEmpty()) {
                chatsCollection.document()
            } else {
                chatsCollection.document(chat.id)
            }

            val chatWithId = chat.copy(id = docRef.id)
            docRef.set(chatWithId).await()
            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error creating chat: ${e.message}")
            Result.failure(e)
        }
    }

    fun getChatsFlow(userId: String): Flow<List<Chat>> = callbackFlow {
        try {
            val subscription = chatsCollection
                .whereEqualTo("userId", userId)
                .orderBy("updatedAt", Query.Direction.DESCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreService", "Error loading chats: ${error.message}")

                        // If index error, return empty list instead of crashing
                        if (error.message?.contains("index") == true) {
                            Log.w("FirestoreService", "Index not ready yet. Please wait 2-3 minutes.")
                        }

                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val chats = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Chat::class.java)
                        } catch (e: Exception) {
                            Log.e("FirestoreService", "Error parsing chat: ${e.message}")
                            null
                        }
                    } ?: emptyList()

                    trySend(chats)
                }

            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Exception in getChatsFlow: ${e.message}")
            trySend(emptyList())
            close(e)
        }
    }

    suspend fun updateChat(chat: Chat): Result<Unit> {
        return try {
            chatsCollection.document(chat.id).set(chat).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error updating chat: ${e.message}")
            Result.failure(e)
        }
    }

    suspend fun deleteChat(chatId: String): Result<Unit> {
        return try {
            // Delete chat
            chatsCollection.document(chatId).delete().await()

            // Delete all messages in chat
            val messages = messagesCollection
                .whereEqualTo("chatId", chatId)
                .get()
                .await()

            val batch = firestore.batch()
            messages.documents.forEach { batch.delete(it.reference) }
            batch.commit().await()

            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error deleting chat: ${e.message}")
            Result.failure(e)
        }
    }

    // ===== MESSAGE OPERATIONS =====

    suspend fun sendMessage(message: Message): Result<String> {
        return try {
            val docRef = if (message.id.isEmpty()) {
                messagesCollection.document()
            } else {
                messagesCollection.document(message.id)
            }

            val messageWithId = message.copy(id = docRef.id)
            docRef.set(messageWithId).await()

            // Update chat's updatedAt timestamp
            chatsCollection.document(message.chatId).update(
                mapOf(
                    "updatedAt" to System.currentTimeMillis(),
                    "messageCount" to com.google.firebase.firestore.FieldValue.increment(1)
                )
            ).await()

            Result.success(docRef.id)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error sending message: ${e.message}")
            Result.failure(e)
        }
    }

    fun getMessagesFlow(chatId: String): Flow<List<Message>> = callbackFlow {
        try {
            val subscription = messagesCollection
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshot, error ->
                    if (error != null) {
                        Log.e("FirestoreService", "Error loading messages: ${error.message}")

                        if (error.message?.contains("index") == true) {
                            Log.w("FirestoreService", "Index not ready yet. Please wait 2-3 minutes.")
                        }

                        trySend(emptyList())
                        return@addSnapshotListener
                    }

                    val messages = snapshot?.documents?.mapNotNull { doc ->
                        try {
                            doc.toObject(Message::class.java)
                        } catch (e: Exception) {
                            Log.e("FirestoreService", "Error parsing message: ${e.message}")
                            null
                        }
                    } ?: emptyList()

                    trySend(messages)
                }

            awaitClose { subscription.remove() }
        } catch (e: Exception) {
            Log.e("FirestoreService", "Exception in getMessagesFlow: ${e.message}")
            trySend(emptyList())
            close(e)
        }
    }

    suspend fun getFirstMessage(chatId: String): Result<Message?> {
        return try {
            val snapshot = messagesCollection
                .whereEqualTo("chatId", chatId)
                .orderBy("timestamp", Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .await()

            val message = snapshot.documents.firstOrNull()?.toObject(Message::class.java)
            Result.success(message)
        } catch (e: Exception) {
            Log.e("FirestoreService", "Error getting first message: ${e.message}")
            Result.failure(e)
        }
    }
}
