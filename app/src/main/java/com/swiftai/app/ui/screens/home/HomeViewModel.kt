package com.swiftai.app.ui.screens.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.data.repository.UserRepository
import com.swiftai.app.domain.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val chatRepository: ChatRepository,
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        // Add delay to ensure Firebase user is fully loaded
        viewModelScope.launch {
            if (currentUserId.isNotEmpty()) {
                loadChats()
                loadUserData()
            } else {
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            try {
                if (currentUserId.isEmpty()) return@launch

                chatRepository.getChatsFlow(currentUserId).collect { chats ->
                    _uiState.value = _uiState.value.copy(
                        chats = chats,
                        isLoading = false
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    chats = emptyList(),
                    isLoading = false
                )
            }
        }
    }

    private fun loadUserData() {
        viewModelScope.launch {
            try {
                if (currentUserId.isEmpty()) return@launch

                userRepository.getUserFlow(currentUserId).collect { user ->
                    _uiState.value = _uiState.value.copy(
                        userTier = user?.subscriptionTier ?: "free",
                        pinnedTools = user?.pinnedTools ?: emptyList()
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    userTier = "free",
                    pinnedTools = emptyList()
                )
            }
        }
    }

    fun createNewChat(): String {
        val chatId = UUID.randomUUID().toString()
        val newChat = Chat(
            id = chatId,
            userId = currentUserId,
            title = "New Chat",
            createdAt = System.currentTimeMillis(),
            lastMessageTime = System.currentTimeMillis(),
            model = "gemini-pro" // Default to Gemini
        )

        viewModelScope.launch {
            chatRepository.createChat(newChat)
        }

        return chatId
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            chatRepository.deleteChat(chatId)
        }
    }

    fun unpinTool(toolId: String) {
        viewModelScope.launch {
            val currentPinned = _uiState.value.pinnedTools.toMutableList()
            currentPinned.remove(toolId)
            userRepository.updatePinnedTools(currentUserId, currentPinned)
        }
    }
}

data class HomeUiState(
    val chats: List<Chat> = emptyList(),
    val userTier: String = "free",
    val pinnedTools: List<String> = emptyList(),
    val isLoading: Boolean = true
)
