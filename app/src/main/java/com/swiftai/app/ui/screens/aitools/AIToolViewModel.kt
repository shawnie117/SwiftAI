package com.swiftai.app.ui.screens.aitools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.swiftai.app.data.repository.ChatRepository
import com.swiftai.app.data.repository.UserRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIToolsViewModel @Inject constructor(
    private val userRepository: UserRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIToolsUiState())
    val uiState = _uiState.asStateFlow()

    private val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    init {
        loadPinnedTools()
    }

    private fun loadPinnedTools() {
        viewModelScope.launch {
            userRepository.getUserFlow(currentUserId).collect { user ->
                _uiState.value = _uiState.value.copy(
                    pinnedTools = user?.pinnedTools ?: emptyList()
                )
            }
        }
    }

    fun togglePin(toolId: String) {
        viewModelScope.launch {
            val currentPinned = _uiState.value.pinnedTools.toMutableList()

            if (currentPinned.contains(toolId)) {
                currentPinned.remove(toolId)
            } else {
                currentPinned.add(toolId)
            }

            userRepository.updatePinnedTools(currentUserId, currentPinned)
        }
    }
}

data class AIToolsUiState(
    val pinnedTools: List<String> = emptyList()
)

// ViewModel for individual AI Tool detail screen
@HiltViewModel
class AIToolViewModel @Inject constructor(
    private val chatRepository: ChatRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIToolUiState())
    val uiState = _uiState.asStateFlow()

    fun processInput(toolId: String, input: String, modelId: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                error = null,
                result = ""
            )

            try {
                // Create a prompt based on the tool type
                val prompt = createPromptForTool(toolId, input)

                // TODO: Implement actual API call to process the input
                // For now, simulate a response
                kotlinx.coroutines.delay(1500)

                val result = "This is a simulated result for $toolId.\n\nInput: $input\n\nModel: $modelId"

                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    result = result
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = e.message ?: "An error occurred"
                )
            }
        }
    }

    private fun createPromptForTool(toolId: String, input: String): String {
        return when (toolId) {
            "translation" -> {
                val parts = input.split("|")
                val text = parts.getOrNull(0) ?: input
                val from = parts.getOrNull(1) ?: "English"
                val to = parts.getOrNull(2) ?: "Spanish"
                "Translate the following text from $from to $to: $text"
            }
            "text_summarization" -> "Summarize the following text: $input"
            "grammar_check" -> "Check and correct the grammar in the following text: $input"
            "code_assistant" -> "Generate code for: $input"
            "code_review" -> "Review the following code and provide suggestions: $input"
            "image_generation" -> "Generate an image description for: $input"
            "creative_writing" -> "Write creative content about: $input"
            else -> input
        }
    }
}

data class AIToolUiState(
    val result: String = "",
    val isLoading: Boolean = false,
    val error: String? = null
)

