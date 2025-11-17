package com.swiftai.app.domain.model

import java.util.UUID

data class Message(
    val id: String = UUID.randomUUID().toString(),
    val chatId: String = "",
    val content: String = "",
    val isUser: Boolean = false,  // ✅ Use this (not 'role')
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false  // ✅ ADD THIS for error handling
) {
    // ✅ ADD: No-arg constructor for Firestore
    constructor() : this("", "", "", false, 0L, false)

    // ✅ OPTIONAL: Helper property for compatibility
    val role: String
        get() = if (isUser) "user" else "assistant"
}
