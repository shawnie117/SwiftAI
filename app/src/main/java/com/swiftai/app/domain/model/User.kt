package com.swiftai.app.domain.model

data class User(
    val uid: String = "",
    val email: String = "",
    val displayName: String = "",
    val photoURL: String = "",  // ✅ ADD THIS
    val subscriptionTier: String = "free",  // Changed from subscriptionType
    val subscriptionExpiry: Long = 0L,
    val pinnedTools: List<String> = emptyList(),
    val createdAt: Long = System.currentTimeMillis()
) {
    // ✅ ADD: No-arg constructor for Firestore
    constructor() : this("", "", "", "", "free", 0L, emptyList(), System.currentTimeMillis())
}
