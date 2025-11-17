package com.swiftai.app.data.remote.firebase

import android.content.Context
import android.content.Intent
import android.util.Log
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.UserProfileChangeRequest
import com.swiftai.app.R
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.tasks.await
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAuthService @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val auth: FirebaseAuth = FirebaseAuth.getInstance()

    val currentUser: FirebaseUser?
        get() = auth.currentUser

    val isUserLoggedIn: Boolean
        get() = currentUser != null

    // Email/Password Sign Up
    suspend fun signUpWithEmail(
        email: String,
        password: String,
        displayName: String
    ): Result<FirebaseUser> {
        return try {
            val result = auth.createUserWithEmailAndPassword(email, password).await()
            val user = result.user

            if (user != null) {
                // Update display name
                val profileUpdates = UserProfileChangeRequest.Builder()
                    .setDisplayName(displayName)
                    .build()
                user.updateProfile(profileUpdates).await()
                Log.d("FirebaseAuthService", "User created successfully: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("FirebaseAuthService", "User is null after creation")
                Result.failure(Exception("User creation failed"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error in signUpWithEmail: ${e.message}")
            Result.failure(e)
        }
    }

    // Email/Password Sign In
    suspend fun signInWithEmail(email: String, password: String): Result<FirebaseUser> {
        return try {
            val result = auth.signInWithEmailAndPassword(email, password).await()
            val user = result.user
            if (user != null) {
                Log.d("FirebaseAuthService", "User signed in: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("FirebaseAuthService", "User is null after sign in")
                Result.failure(Exception("Sign in failed"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error in signInWithEmail: ${e.message}")
            Result.failure(e)
        }
    }

    // Get Google Sign-In Client
    fun getGoogleSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(context.getString(R.string.default_web_client_id))
            .requestEmail()
            .build()

        return GoogleSignIn.getClient(context, gso)
    }

    // Get Google Sign-In Intent
    fun getGoogleSignInIntent(): Intent {
        return getGoogleSignInClient().signInIntent
    }

    // Sign In with Google
    suspend fun signInWithGoogle(account: GoogleSignInAccount): Result<FirebaseUser> {
        return try {
            Log.d("FirebaseAuthService", "Starting Google sign-in with token")
            val credential = GoogleAuthProvider.getCredential(account.idToken, null)
            val result = auth.signInWithCredential(credential).await()
            val user = result.user

            if (user != null) {
                Log.d("FirebaseAuthService", "Google sign-in successful: ${user.uid}")
                Result.success(user)
            } else {
                Log.e("FirebaseAuthService", "User is null after Google sign-in")
                Result.failure(Exception("Google sign in failed"))
            }
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error in signInWithGoogle: ${e.message}", e)
            Result.failure(e)
        }
    }

    // Sign Out
    fun signOut() {
        try {
            auth.signOut()
            getGoogleSignInClient().signOut()
            Log.d("FirebaseAuthService", "User signed out successfully")
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error signing out: ${e.message}")
        }
    }

    // Delete Account
    suspend fun deleteAccount(): Result<Unit> {
        return try {
            currentUser?.delete()?.await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error deleting account: ${e.message}")
            Result.failure(e)
        }
    }

    // Reset Password
    suspend fun sendPasswordResetEmail(email: String): Result<Unit> {
        return try {
            auth.sendPasswordResetEmail(email).await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e("FirebaseAuthService", "Error sending password reset: ${e.message}")
            Result.failure(e)
        }
    }
}
