package com.swiftai.app.domain.model

data class User(
    val id: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoURL: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val subscriptionType: String = "free" // free, premium
)
