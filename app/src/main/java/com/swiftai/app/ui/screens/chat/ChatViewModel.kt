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
        viewModelScope.launch {
            _uiState.update { it.copy(currentChatId = chatId, isLoading = true) }
            repository.getMessagesFlow(chatId)
                .catch { e ->
                    Log.e("ChatViewModel", "Error loading messages", e)
                    _uiState.update { it.copy(error = e.message, isLoading = false) }
                }
                .collect { messages ->
                    _uiState.update { it.copy(messages = messages, isLoading = false, error = null) }
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

        // Clear input immediately
        _uiState.update { it.copy(inputText = "", isLoading = true) }

        sendMessage(content)
    }

    fun sendMessage(content: String) {
        val chatId = _uiState.value.currentChatId
        if (chatId.isBlank() || content.isBlank()) return

        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            Log.d("ChatViewModel", "Sending message: $content")

            val message = Message(
                id = UUID.randomUUID().toString(),
                chatId = chatId,
                content = content,
                isUser = true,
                timestamp = System.currentTimeMillis()
            )

            val result = repository.sendMessage(message)

            when {
                result.isSuccess -> {
                    Log.d("ChatViewModel", "Message sent successfully")
                    _uiState.update { it.copy(isLoading = false, error = null) }
                }
                else -> {
                    val error = result.exceptionOrNull()
                    Log.e("ChatViewModel", "Error sending message: ${error?.message}")
                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            error = error?.message ?: "Failed to send message"
                        )
                    }
                }
            }
        }
    }

    fun createNewChat() {
        viewModelScope.launch {
            val newChat = Chat(
                id = UUID.randomUUID().toString(),
                userId = currentUserId,
                title = "New Chat",
                lastMessageTime = System.currentTimeMillis()
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
    val isLoading: Boolean = false,
    val error: String? = null
)
