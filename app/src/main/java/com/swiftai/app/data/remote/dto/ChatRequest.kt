package com.swiftai.app.data.remote.dto

data class ChatRequest(
    val prompt: String,
    val model: String = "swiftai-mini",
    val max_length: Int = 100
)
