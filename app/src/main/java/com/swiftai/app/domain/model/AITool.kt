package com.swiftai.app.domain.model

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.graphics.vector.ImageVector

data class AITool(
    val id: String,
    val name: String,
    val description: String,
    val icon: ImageVector,
    val isPremium: Boolean,
    val tier: String, // "free", "pro", "max"
    val category: String,
    val modelId: String // Which AI model to use
)

object AITools {
    val tools = listOf(
        // Text Tools (Free)
        AITool(
            id = "text_generation",
            name = "Text Generation",
            description = "Generate creative text, stories, and articles",
            icon = Icons.Default.Create,
            isPremium = false,
            tier = "free",
            category = "Text",
            modelId = "swiftai-standard"
        ),
        AITool(
            id = "text_summarization",
            name = "Text Summarizer",
            description = "Summarize long articles and documents",
            icon = Icons.Default.Description,
            isPremium = false,
            tier = "free",
            category = "Text",
            modelId = "swiftai-standard"
        ),
        AITool(
            id = "grammar_check",
            name = "Grammar Checker",
            description = "Check and improve your writing",
            icon = Icons.Default.Spellcheck,
            isPremium = false,
            tier = "free",
            category = "Text",
            modelId = "swiftai-mini"
        ),

        // Translation (Free)
        AITool(
            id = "translation",
            name = "Translation",
            description = "Translate text between 100+ languages",
            icon = Icons.Default.Translate,
            isPremium = false,
            tier = "free",
            category = "Language",
            modelId = "swiftai-standard"
        ),

        // Code Tools (Pro)
        AITool(
            id = "code_assistant",
            name = "Code Assistant",
            description = "Generate, debug, and explain code",
            icon = Icons.Default.Code,
            isPremium = true,
            tier = "pro",
            category = "Development",
            modelId = "swiftai-pro"
        ),
        AITool(
            id = "code_review",
            name = "Code Review",
            description = "Review and improve your code",
            icon = Icons.Default.BugReport,
            isPremium = true,
            tier = "pro",
            category = "Development",
            modelId = "swiftai-pro"
        ),

        // Creative Tools (Pro)
        AITool(
            id = "image_generation",
            name = "Image Generation",
            description = "Create AI-generated images from text",
            icon = Icons.Default.Image,
            isPremium = true,
            tier = "pro",
            category = "Creative",
            modelId = "swiftai-pro"
        ),
        AITool(
            id = "creative_writing",
            name = "Creative Writing",
            description = "Write poems, songs, and creative content",
            icon = Icons.Default.AutoStories,
            isPremium = true,
            tier = "pro",
            category = "Creative",
            modelId = "swiftai-pro"
        ),

        // Analysis Tools (Pro)
        AITool(
            id = "document_analyzer",
            name = "Document Analyzer",
            description = "Analyze and extract insights from documents",
            icon = Icons.Default.Analytics,
            isPremium = true,
            tier = "pro",
            category = "Analysis",
            modelId = "swiftai-pro"
        ),
        AITool(
            id = "data_analysis",
            name = "Data Analysis",
            description = "Analyze data and generate insights",
            icon = Icons.Default.BarChart,
            isPremium = true,
            tier = "pro",
            category = "Analysis",
            modelId = "swiftai-pro"
        ),

        // Max Tier Tools
        AITool(
            id = "text_to_speech",
            name = "Text-to-Speech",
            description = "Convert text to natural speech",
            icon = Icons.Default.RecordVoiceOver,
            isPremium = true,
            tier = "max",
            category = "Voice",
            modelId = "swiftai-max"
        ),
        AITool(
            id = "speech_to_text",
            name = "Speech-to-Text",
            description = "Transcribe audio to text",
            icon = Icons.Default.Mic,
            isPremium = true,
            tier = "max",
            category = "Voice",
            modelId = "swiftai-max"
        ),
        AITool(
            id = "vision_tools",
            name = "Vision Tools",
            description = "Image recognition and analysis",
            icon = Icons.Default.Visibility,
            isPremium = true,
            tier = "max",
            category = "Vision",
            modelId = "swiftai-max"
        ),
        AITool(
            id = "audio_enhancer",
            name = "Audio Enhancer",
            description = "Enhance and clean audio files",
            icon = Icons.Default.AudioFile,
            isPremium = true,
            tier = "max",
            category = "Audio",
            modelId = "swiftai-max"
        ),
        AITool(
            id = "video_analysis",
            name = "Video Analysis",
            description = "Analyze video content",
            icon = Icons.Default.VideoLibrary,
            isPremium = true,
            tier = "max",
            category = "Vision",
            modelId = "swiftai-max"
        )
    )

    fun getToolById(id: String): AITool? = tools.find { it.id == id }

    fun getToolsByTier(tier: String): List<AITool> = tools.filter { it.tier == tier }

    fun getFreeTools(): List<AITool> = tools.filter { !it.isPremium }

    fun getPremiumTools(): List<AITool> = tools.filter { it.isPremium }

    fun getToolsByCategory(category: String): List<AITool> = tools.filter { it.category == category }
}
