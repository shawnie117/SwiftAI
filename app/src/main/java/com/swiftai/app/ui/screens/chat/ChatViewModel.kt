package com.swiftai.app.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.domain.model.Chat
import com.swiftai.app.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val repository: ChatRepository,
    private val auth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState: StateFlow<ChatUiState> = _uiState.asStateFlow()

    private val currentUserId: String
        get() = auth.currentUser?.uid ?: ""

    init {
        loadChats()
    }

    private fun loadChats() {
        viewModelScope.launch {
            repository.getChatsFlow(currentUserId)
                .catch { e ->
                    Log.e("ChatViewModel", "Error loading chats", e)
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { chats ->
                    _uiState.update { it.copy(chats = chats, error = null) }
                }
        }
    }

    fun loadMessages(chatId: String) {
        // Don't reload if already listening to this chat
        if (_uiState.value.currentChatId == chatId && _uiState.value.messages.isNotEmpty()) {
            Log.d("ChatViewModel", "Already listening to chat $chatId with ${_uiState.value.messages.size} messages")
            return
        }

        viewModelScope.launch {
            Log.d("ChatViewModel", "Loading messages for chat: $chatId")
            _uiState.update { it.copy(currentChatId = chatId, isLoading = true) }
            repository.getMessagesFlow(chatId)
                .distinctUntilChanged() // Prevent duplicate emissions
                .catch { e ->
                    Log.e("ChatViewModel", "Error loading messages", e)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { messages ->
                    Log.d("ChatViewModel", "Received ${messages.size} messages from Firestore")
                    _uiState.update {
                        it.copy(
                            messages = messages,
                            isLoading = false,
                            isThinking = false,
                            error = null
                        )
                    }
                }
        }
    }

    // Alias for compatibility with ChatScreen
    fun loadChat(chatId: String) = loadMessages(chatId)

    fun updateInputText(text: String) {
        _uiState.update { it.copy(inputText = text) }
    }

    fun sendMessage() {
        val content = _uiState.value.inputText.trim()
        if (content.isEmpty()) return

        // Clear input and show thinking
        _uiState.update { it.copy(inputText = "", isThinking = true) }

        // Ensure we have a chat
        var chatId = _uiState.value.currentChatId
        if (chatId.isBlank()) {
            chatId = UUID.randomUUID().toString()
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
            viewModelScope.launch {
                repository.createChat(newChat)
                // Start listening to messages for this new chat
                loadMessages(chatId)
            }
            _uiState.update { it.copy(currentChatId = chatId) }
        }

        // Create user message
        val userMessage = Message(
            id = UUID.randomUUID().toString(),
            chatId = chatId,
            content = content,
            isUser = true,
            timestamp = System.currentTimeMillis()
        )

        Log.d("ChatViewModel", "Sending message: $content to chat: $chatId")

        // Send message - Firestore listener will update UI automatically
        viewModelScope.launch {
            val result = repository.sendMessage(userMessage)

            // Stop thinking indicator when done (success or failure)
            _uiState.update { it.copy(isThinking = false) }

            if (result.isFailure) {
                val errorText = result.exceptionOrNull()?.message ?: "Failed to send message"
                Log.e("ChatViewModel", "Error sending message: $errorText")
                _uiState.update { it.copy(error = errorText) }
            } else {
                Log.d("ChatViewModel", "Message sent successfully")
            }
        }
    }

    fun createNewChat() {
        viewModelScope.launch {
            val currentTime = System.currentTimeMillis()
            val newChat = Chat(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "New Chat",
                createdAt = currentTime,
                updatedAt = currentTime,
                lastMessageTime = currentTime,
                model = "gemini-pro",
                messageCount = 0
            )
            repository.createChat(newChat)
            _uiState.update { it.copy(currentChatId = newChat.id) }
            loadMessages(newChat.id)
        }
    }

    fun deleteChat(chatId: String) {
        viewModelScope.launch {
            repository.deleteChat(chatId)
            if (_uiState.value.currentChatId == chatId) {
                _uiState.update { it.copy(currentChatId = "", messages = emptyList()) }
            }
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class ChatUiState(
    val chats: List<Chat> = emptyList(),
    val messages: List<Message> = emptyList(),
    val currentChatId: String = "",
    val inputText: String = "",
    val isLoading: Boolean = false, // retains purpose for initial loads
    val isThinking: Boolean = false, // NEW flag for AI thinking/typing indicator
    val error: String? = null
)
