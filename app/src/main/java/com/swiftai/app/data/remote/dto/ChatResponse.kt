package com.swiftai.app.data.remote.dto

import com.swiftai.app.domain.model.Message

data class ChatResponse(
    val success: Boolean,
    val response: String,
    val model: String,
    val chat_id: String,
    val history: List<Message>
)
