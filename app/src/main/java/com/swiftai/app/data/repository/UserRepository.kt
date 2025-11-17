package com.swiftai.app.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.swiftai.app.domain.model.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getUserFlow(userId: String): Flow<User?> = callbackFlow {
        val listener = firestore.collection("users")
            .document(userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }

                val user = snapshot?.toObject(User::class.java)
                trySend(user)
            }

        awaitClose { listener.remove() }
    }

    suspend fun getUser(userId: String): User? {
        return try {
            firestore.collection("users")
                .document(userId)
                .get()
                .await()
                .toObject(User::class.java)
        } catch (e: Exception) {
            null
        }
    }

    suspend fun createUser(user: User) {
        firestore.collection("users")
            .document(user.uid)
            .set(user)
            .await()
    }

    suspend fun updateSubscriptionTier(userId: String, tier: String) {
        firestore.collection("users")
            .document(userId)
            .update("subscriptionTier", tier)
            .await()
    }

    suspend fun updatePinnedTools(userId: String, pinnedTools: List<String>) {
        firestore.collection("users")
            .document(userId)
            .update("pinnedTools", pinnedTools)
            .await()
    }
}
