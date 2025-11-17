package com.swiftai.app.domain.model

data class AIModel(
    val id: String,
    val name: String,
    val description: String,
    val isPremium: Boolean,
    val icon: String,
    val maxLength: Int
)

object AIModels {
    val models = listOf(
        AIModel(
            id = "swiftai-mini",
            name = "SwiftAI Mini",
            description = "Fast and efficient for quick responses",
            isPremium = false,
            icon = "⚡",
            maxLength = 100
        ),
        AIModel(
            id = "swiftai-standard",
            name = "SwiftAI Standard",
            description = "Balanced performance and quality",
            isPremium = false,
            icon = "💫",
            maxLength = 200
        ),
        AIModel(
            id = "swiftai-pro",
            name = "SwiftAI Pro",
            description = "Advanced reasoning and accuracy",
            isPremium = true,
            icon = "🚀",
            maxLength = 500
        ),
        AIModel(
            id = "swiftai-max",
            name = "SwiftAI Max",
            description = "Ultimate AI capabilities",
            isPremium = true,
            icon = "👑",
            maxLength = 1000
        )
    )
}
