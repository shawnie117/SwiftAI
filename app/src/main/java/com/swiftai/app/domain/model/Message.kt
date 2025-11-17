package com.swiftai.app.domain.model

data class Message(
    val id: String = "",
    val chatId: String = "",
    val content: String = "",
    val role: String = "", // "user" or "assistant"
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false
)
