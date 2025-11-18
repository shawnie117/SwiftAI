package com.swiftai.app.ui.screens.home

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.data.repository.UserRepository
import com.swiftai.app.domain.model.Chat
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.distinctUntilChanged
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

    private var allChats: List<Chat> = emptyList()

    private val currentUserId: String
        get() = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        Log.d("HomeViewModel", "Initializing HomeViewModel")
        viewModelScope.launch {
            val userId = currentUserId
            Log.d("HomeViewModel", "Current user ID: $userId")

            if (userId.isNotEmpty()) {
                Log.d("HomeViewModel", "User authenticated, loading chats...")
                loadChats()
                loadUserData()
            } else {
                Log.w("HomeViewModel", "No user authenticated!")
                _uiState.value = _uiState.value.copy(isLoading = false)
            }
        }
    }

    private fun loadChats() {
        viewModelScope.launch {
            try {
                val userId = currentUserId
                if (userId.isEmpty()) {
                    Log.e("HomeViewModel", "Cannot load chats: userId is empty")
                    return@launch
                }

                Log.d("HomeViewModel", "Starting to load chats for user: $userId")

                chatRepository.getChatsFlow(userId)
                    .distinctUntilChanged() // Prevent duplicate emissions
                    .catch { e ->
                        Log.e("HomeViewModel", "Error loading chats: ${e.message}", e)
                        _uiState.value = _uiState.value.copy(
                            chats = emptyList(),
                            isLoading = false
                        )
                    }
                    .collect { chats ->
                        Log.d("HomeViewModel", "Received ${chats.size} chats from Firestore")
                        allChats = chats
                        applySearchFilter()
                    }
            } catch (e: Exception) {
                Log.e("HomeViewModel", "Exception in loadChats: ${e.message}", e)
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
        val currentTime = System.currentTimeMillis()
        val newChat = Chat(
            id = chatId,
            userId = currentUserId,
            title = "New Chat",
            createdAt = currentTime,
            updatedAt = currentTime,
            lastMessageTime = currentTime,
            model = "gemini-pro",
            messageCount = 0
        )

        Log.d("HomeViewModel", "Creating new chat: $chatId for user: $currentUserId")

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

    fun refresh() {
        Log.d("HomeViewModel", "Manual refresh triggered")
        _uiState.value = _uiState.value.copy(isLoading = true)
        loadChats()
        loadUserData()
    }

    fun onSearchQueryChanged(query: String) {
        _uiState.value = _uiState.value.copy(searchQuery = query)
        applySearchFilter()
    }

    private fun applySearchFilter() {
        val query = _uiState.value.searchQuery
        val filteredChats = if (query.isBlank()) {
            allChats
        } else {
            allChats.filter { chat ->
                chat.title.contains(query, ignoreCase = true)
            }
        }
        _uiState.value = _uiState.value.copy(
            chats = filteredChats,
            isLoading = false
        )
    }

    fun getUserId(): String = currentUserId
}

data class HomeUiState(
    val chats: List<Chat> = emptyList(),
    val userTier: String = "free",
    val pinnedTools: List<String> = emptyList(),
    val isLoading: Boolean = true,
    val searchQuery: String = ""
)
