package com.swiftai.app.domain.model

data class AIModel(
    val id: String,
    val name: String,
    val description: String,
    val isPremium: Boolean,
    val tier: String, // "free", "pro", "max"
    val icon: String,
    val maxLength: Int,
    val capabilities: List<String>
)

object AIModels {
    val models = listOf(
        AIModel(
            id = "swiftai-mini",
            name = "SwiftAI Mini",
            description = "Fast and efficient for quick responses",
            isPremium = false,
            tier = "free",
            icon = "⚡",
            maxLength = 100,
            capabilities = listOf("Text Chat", "Basic Q&A")
        ),
        AIModel(
            id = "swiftai-standard",
            name = "SwiftAI Standard",
            description = "Balanced performance and quality",
            isPremium = false,
            tier = "free",
            icon = "💫",
            maxLength = 200,
            capabilities = listOf("Text Chat", "Q&A", "Simple Tasks")
        ),
        AIModel(
            id = "swiftai-pro",
            name = "SwiftAI Pro",
            description = "Advanced reasoning and creativity",
            isPremium = true,
            tier = "pro",
            icon = "🚀",
            maxLength = 500,
            capabilities = listOf("Advanced Chat", "Code", "Analysis", "Creative Writing")
        ),
        AIModel(
            id = "swiftai-max",
            name = "SwiftAI Max",
            description = "Ultimate AI with all capabilities",
            isPremium = true,
            tier = "max",
            icon = "👑",
            maxLength = 1000,
            capabilities = listOf("All Features", "Image Gen", "Voice", "Expert Analysis")
        )
    )

    fun getModelById(id: String): AIModel? = models.find { it.id == id }

    fun getModelsByTier(tier: String): List<AIModel> = models.filter { it.tier == tier }

    fun getFreeModels(): List<AIModel> = models.filter { !it.isPremium }

    fun getPremiumModels(): List<AIModel> = models.filter { it.isPremium }
}
