package com.swiftai.app.data.repository

import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.firebase.auth.FirebaseUser
import com.swiftai.app.data.remote.firebase.FirebaseAuthService
import com.swiftai.app.data.remote.firebase.FirestoreService
import com.swiftai.app.domain.model.User
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class AuthRepository @Inject constructor(
    private val authService: FirebaseAuthService,
    private val firestoreService: FirestoreService
) {
    val currentUser: FirebaseUser?
        get() = authService.currentUser

    val isUserLoggedIn: Boolean
        get() = authService.isUserLoggedIn

    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<User> {
        return try {
            val result = authService.signUpWithEmail(email, password, displayName)

            if (result.isSuccess) {
                val firebaseUser = result.getOrThrow()
                val user = User(
                    uid = firebaseUser.uid,
                    email = email,
                    displayName = displayName,
                    subscriptionTier = "free",
                    createdAt = System.currentTimeMillis()
                )

                firestoreService.createUser(user)
                Result.success(user)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Sign up failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithEmail(email: String, password: String): Result<User> {
        return try {
            val result = authService.signInWithEmail(email, password)

            if (result.isSuccess) {
                val firebaseUser = result.getOrThrow()
                val userResult = firestoreService.getUser(firebaseUser.uid)

                if (userResult.isSuccess) {
                    val user = userResult.getOrThrow()
                    if (user != null) {
                        Result.success(user)
                    } else {
                        // User document doesn't exist, create it
                        val newUser = User(
                            uid = firebaseUser.uid,
                            email = firebaseUser.email ?: email,
                            displayName = firebaseUser.displayName ?: "",
                            subscriptionTier = "free",
                            createdAt = System.currentTimeMillis()
                        )
                        firestoreService.createUser(newUser)
                        Result.success(newUser)
                    }
                } else {
                    Result.failure(userResult.exceptionOrNull() ?: Exception("Failed to get user"))
                }
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<User> {
        return try {
            val result = authService.signInWithGoogle(account)

            if (result.isSuccess) {
                val firebaseUser = result.getOrThrow()

                // Check if user exists
                val userResult = firestoreService.getUser(firebaseUser.uid)

                val user = if (userResult.isSuccess && userResult.getOrNull() != null) {
                    // Existing user
                    userResult.getOrThrow()!!
                } else {
                    // New user - create profile
                    val newUser = User(
                        uid = firebaseUser.uid,
                        email = firebaseUser.email ?: "",
                        displayName = firebaseUser.displayName ?: "",
                        subscriptionTier = "free",
                        createdAt = System.currentTimeMillis()
                    )
                    firestoreService.createUser(newUser)
                    newUser
                }

                Result.success(user)
            } else {
                Result.failure(result.exceptionOrNull() ?: Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun getGoogleSignInClient() = authService.getGoogleSignInClient()

    fun signOut() = authService.signOut()

    suspend fun sendPasswordResetEmail(email: String) = authService.sendPasswordResetEmail(email)
}
