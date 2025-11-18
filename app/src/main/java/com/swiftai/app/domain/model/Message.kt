package com.swiftai.app.domain.model

data class Message(
    val id: String,
    val chatId: String,
    val content: String,
    val isUser: Boolean,
    val timestamp: Long
)
