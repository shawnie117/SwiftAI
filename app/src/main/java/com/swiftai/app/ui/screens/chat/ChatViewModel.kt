package com.swiftai.app.ui.screens.chat

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.domain.model.Chat
import com.swiftai.app.domain.model.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.launch
import java.util.UUID
import javax.inject.Inject

@HiltViewModel
class ChatViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ChatUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    private var currentChatId = ""
    private var currentChat: Chat? = null

    fun loadChat(chatId: String) {
        currentChatId = chatId

        viewModelScope.launch {
            try {
                // Load messages
                chatRepository.getMessagesFlow(chatId)
                    .catch { e ->
                        Log.e("ChatViewModel", "Error loading messages: ${e.message}")
                        emit(emptyList())
                    }
                    .collect { messages ->
                        _uiState.value = _uiState.value.copy(
                            messages = messages,
                            isLoading = false
                        )
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error in loadChat messages: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    messages = emptyList(),
                    isLoading = false
                )
            }
        }

        // Load chat details
        viewModelScope.launch {
            try {
                chatRepository.getChatsFlow(currentUserId)
                    .catch { e ->
                        Log.e("ChatViewModel", "Error loading chat details: ${e.message}")
                        emit(emptyList())
                    }
                    .collect { chats ->
                        currentChat = chats.find { it.id == chatId }
                        currentChat?.let { chat ->
                            _uiState.value = _uiState.value.copy(
                                chatTitle = chat.title,
                                selectedModel = chat.model
                            )
                        }
                    }
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error loading chat details: ${e.message}")
            }
        }
    }

    fun onMessageChange(message: String) {
        _uiState.value = _uiState.value.copy(inputMessage = message)
    }

    fun onModelChange(model: String) {
        _uiState.value = _uiState.value.copy(selectedModel = model)

        // Update chat model
        currentChat?.let { chat ->
            viewModelScope.launch {
                try {
                    chatRepository.updateChat(chat.copy(model = model))
                } catch (e: Exception) {
                    Log.e("ChatViewModel", "Error updating model: ${e.message}")
                }
            }
        }
    }

    fun sendMessage() {
        val messageText = _uiState.value.inputMessage.trim()
        if (messageText.isEmpty() || _uiState.value.isSending) return

        _uiState.value = _uiState.value.copy(
            inputMessage = "",
            isSending = true
        )

        viewModelScope.launch {
            try {
                // Create user message
                val userMessage = Message(
                    id = UUID.randomUUID().toString(),
                    chatId = currentChatId,
                    content = messageText,
                    role = "user",
                    timestamp = System.currentTimeMillis()
                )

                // Save user message
                chatRepository.sendMessage(userMessage)

                // Generate chat title if this is the first message
                if (_uiState.value.messages.isEmpty()) {
                    val title = chatRepository.generateChatTitle(messageText)
                    currentChat?.let { chat ->
                        chatRepository.updateChat(chat.copy(title = title))
                    }
                }

                // Get AI response
                val maxLength = when (_uiState.value.selectedModel) {
                    "swiftai-mini" -> 100
                    "swiftai-standard" -> 200
                    "swiftai-pro" -> 500
                    "swiftai-max" -> 1000
                    else -> 100
                }

                val aiResponseResult = chatRepository.getAIResponse(
                    prompt = messageText,
                    model = _uiState.value.selectedModel,
                    maxLength = maxLength
                )

                val aiMessage = if (aiResponseResult.isSuccess) {
                    Message(
                        id = UUID.randomUUID().toString(),
                        chatId = currentChatId,
                        content = aiResponseResult.getOrNull() ?: "Sorry, I couldn't generate a response.",
                        role = "assistant",
                        timestamp = System.currentTimeMillis()
                    )
                } else {
                    Message(
                        id = UUID.randomUUID().toString(),
                        chatId = currentChatId,
                        content = aiResponseResult.exceptionOrNull()?.message ?: "Error: Could not connect to AI service",
                        role = "assistant",
                        timestamp = System.currentTimeMillis(),
                        isError = true
                    )
                }

                // Save AI message
                chatRepository.sendMessage(aiMessage)

                _uiState.value = _uiState.value.copy(isSending = false)
            } catch (e: Exception) {
                Log.e("ChatViewModel", "Error sending message: ${e.message}")
                _uiState.value = _uiState.value.copy(isSending = false)
            }
        }
    }
}

data class ChatUiState(
    val messages: List<Message> = emptyList(),
    val inputMessage: String = "",
    val chatTitle: String = "New Chat",
    val selectedModel: String = "swiftai-mini",
    val isLoading: Boolean = true,
    val isSending: Boolean = false
)
