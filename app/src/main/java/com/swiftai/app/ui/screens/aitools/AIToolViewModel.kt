package com.swiftai.app.ui.screens.aitools

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.swiftai.app.data.remote.api.SwiftAIApi
import com.swiftai.app.domain.model.AIModels
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AIToolViewModel @Inject constructor(
    private val api: SwiftAIApi
) : ViewModel() {

    private val _uiState = MutableStateFlow(AIToolUiState())
    val uiState = _uiState.asStateFlow()

    fun processInput(toolId: String, input: String, modelId: String) {
        viewModelScope.launch {
            try {
                _uiState.value = _uiState.value.copy(isLoading = true, result = "", error = null)

                // Create tool-specific prompt
                val prompt = createPromptForTool(toolId, input)

                // Get model details
                val modelData = AIModels.getModelById(modelId)
                val maxLength = modelData?.maxLength ?: 100

                Log.d("AIToolViewModel", "Processing with model: $modelId, maxLength: $maxLength")
                Log.d("AIToolViewModel", "Prompt: $prompt")

                // Call API with correct model
                val result = api.sendMessage(prompt, modelId, maxLength)

                if (result.isSuccess) {
                    val response = result.getOrNull()?.response ?: "No response"
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        result = response
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        error = "Failed: ${result.exceptionOrNull()?.message}"
                    )
                }
            } catch (e: Exception) {
                Log.e("AIToolViewModel", "Error: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    error = "Error: ${e.message}"
                )
            }
        }
    }

    private fun createPromptForTool(toolId: String, input: String): String {
        return when (toolId) {
            "text_generation" -> {
                "Generate creative and engaging text based on this prompt: $input"
            }
            "text_summarization" -> {
                "Summarize the following text in a concise and clear manner:\n\n$input"
            }
            "grammar_check" -> {
                "Check the following text for grammar, spelling, and punctuation errors. Provide the corrected version:\n\n$input"
            }
            "translation" -> {
                val parts = input.split("|")
                if (parts.size == 3) {
                    val text = parts[0]
                    val fromLang = parts[1]
                    val toLang = parts[2]
                    "Translate the following text from $fromLang to $toLang:\n\n$text"
                } else {
                    "Translate this text: $input"
                }
            }
            "code_assistant" -> {
                "Generate clean, well-commented code for: $input\n\nProvide complete, working code with explanations."
            }
            "code_review" -> {
                "Review the following code and suggest improvements, bug fixes, and best practices:\n\n$input"
            }
            "image_generation" -> {
                "Create a detailed description for an AI image generator based on: $input\n\nInclude visual details, style, mood, and composition."
            }
            "creative_writing" -> {
                "Write creative content (story, poem, or article) based on: $input\n\nBe imaginative and engaging."
            }
            "document_analyzer" -> {
                "Analyze the following document and provide key insights, main points, and important information:\n\n$input"
            }
            "data_analysis" -> {
                "Analyze the following data and provide insights, patterns, and conclusions:\n\n$input"
            }
            "text_to_speech" -> {
                "Convert the following text to speech-ready format with proper pronunciation guides:\n\n$input"
            }
            "speech_to_text" -> {
                "Transcribe and format the following audio content:\n\n$input"
            }
            "vision_tools" -> {
                "Analyze this image description and provide detailed insights:\n\n$input"
            }
            "audio_enhancer" -> {
                "Provide audio enhancement instructions for:\n\n$input"
            }
            "video_analysis" -> {
                "Analyze the following video description:\n\n$input"
            }
            else -> input
        }
    }
}

data class AIToolUiState(
    val isLoading: Boolean = false,
    val result: String = "",
    val error: String? = null
)
