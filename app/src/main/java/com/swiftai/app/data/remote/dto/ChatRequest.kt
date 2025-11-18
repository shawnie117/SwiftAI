package com.swiftai.app.data.remote.dto

data class ChatRequest(
    val prompt: String,
    val model: String = "gemini-pro",
    val max_length: Int = 100,
    val chat_id: String? = null
)
