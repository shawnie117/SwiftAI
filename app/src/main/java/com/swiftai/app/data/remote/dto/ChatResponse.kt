package com.swiftai.app.data.remote.dto

data class ChatResponse(
    val success: Boolean = false,
    val response: String = "",
    val model: String = "",
    val error: String? = null
)
