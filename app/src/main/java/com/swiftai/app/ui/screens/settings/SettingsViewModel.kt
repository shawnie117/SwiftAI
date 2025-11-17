package com.swiftai.app.ui.screens.settings

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.UserProfileChangeRequest
import com.swiftai.app.data.repository.AuthRepository
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.data.repository.UserRepository
import com.swiftai.app.domain.model.User
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository,
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(SettingsUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val currentUserId = currentUser?.uid ?: ""

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            try {
                currentUser?.let { user ->
                    _uiState.value = _uiState.value.copy(
                        displayName = user.displayName ?: "User",
                        email = user.email ?: "",
                        isLoading = false
                    )

                    // Load from Firestore
                    val result = userRepository.getUser(user.uid)
                    if (result.isSuccess) {
                        result.getOrNull()?.let { userData ->
                            _uiState.value = _uiState.value.copy(
                                displayName = userData.displayName,
                                subscriptionType = userData.subscriptionType
                            )
                        }
                    }
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error loading profile: ${e.message}")
            }
        }
    }

    fun updateDisplayName(newName: String) {
        if (newName.isBlank()) return

        viewModelScope.launch {
            try {
                currentUser?.let { user ->
                    // Update Firebase Auth
                    val profileUpdates = UserProfileChangeRequest.Builder()
                        .setDisplayName(newName)
                        .build()
                    user.updateProfile(profileUpdates).await()

                    // Update Firestore
                    val userResult = userRepository.getUser(user.uid)
                    if (userResult.isSuccess) {
                        userResult.getOrNull()?.let { userData ->
                            userRepository.updateUser(userData.copy(displayName = newName))
                        }
                    }

                    // Update UI
                    _uiState.value = _uiState.value.copy(displayName = newName)
                    Log.d("SettingsViewModel", "Name updated successfully")
                }
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error updating name: ${e.message}")
            }
        }
    }

    fun setTheme(isDark: Boolean) {
        _uiState.value = _uiState.value.copy(isDarkTheme = isDark)
        // TODO: Save to DataStore for persistence
    }

    fun clearAllChats() {
        viewModelScope.launch {
            try {
                // Get all user's chats
                chatRepository.getChatsFlow(currentUserId).collect { chats ->
                    chats.forEach { chat ->
                        chatRepository.deleteChat(chat.id)
                    }
                }
                Log.d("SettingsViewModel", "All chats cleared")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error clearing chats: ${e.message}")
            }
        }
    }

    fun exportChatHistory() {
        viewModelScope.launch {
            try {
                // TODO: Implement export functionality
                // Get all chats and messages, convert to JSON/CSV
                Log.d("SettingsViewModel", "Export chat history - Coming soon")
            } catch (e: Exception) {
                Log.e("SettingsViewModel", "Error exporting: ${e.message}")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
        _uiState.value = _uiState.value.copy(isSignedOut = true)
    }
}

data class SettingsUiState(
    val displayName: String = "",
    val email: String = "",
    val subscriptionType: String = "free",
    val isDarkTheme: Boolean = true,
    val isLoading: Boolean = true,
    val isSignedOut: Boolean = false
)
