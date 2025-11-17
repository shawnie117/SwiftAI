package com.swiftai.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.AuthRepository
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.domain.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            try {
                chatRepository.getChatsFlow(currentUserId)
                    .catch { e ->
                        Log.e("HomeViewModel", "Error loading chats: ${e.message}")
                        emit(emptyList())
                    }
                    .collect { chats ->
                        _uiState.value = _uiState.value.copy(
                            chats = chats,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error in loadChats: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    chats = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    fun createNewChat(): String {
        val chatId = java.util.UUID.randomUUID().toString()
        val chat = Chat(
            id = chatId,
            userId = currentUserId,
            title = "New Chat",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            model = "swiftai-mini",
            messageCount = 0
        )

        viewModelScope.launch {
            try {
                chatRepository.createChat(chat)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error creating chat: ${e.message}")
            }
        }

        return chatId
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            try {
                chatRepository.deleteChat(chatId)
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Error deleting chat: ${e.message}")
            }
        }
    }

    fun signOut() {
        authRepository.signOut()
    }
}

data class HomeUiState(
    val chats: List<Chat> = emptyList(),
    val isLoading: Boolean = true
)
