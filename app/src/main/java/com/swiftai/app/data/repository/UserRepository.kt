package com.swiftai.app.data.repository

import com.swiftai.app.data.remote.firebase.FirestoreService
import com.swiftai.app.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserRepository @Inject constructor(
    private val firestoreService: FirestoreService
) {

    suspend fun getUser(userId: String): Result<User?> {
        return firestoreService.getUser(userId)
    }

    suspend fun updateUser(user: User): Result<Unit> {
        return firestoreService.updateUser(user)
    }
}
