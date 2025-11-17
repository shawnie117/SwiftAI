package com.swiftai.app.domain.model

data class Chat(
    val id: String = "",
    val userId: String = "",
    val title: String = "New Chat",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val lastMessageTime: Long = System.currentTimeMillis(),
    val model: String = "swiftai-mini",
    val messageCount: Int = 0
)
