package com.swiftai.app.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val id: String,
    val chatId: String,
    val content: String,
    val role: String,
    val timestamp: Long,
    val isError: Boolean
)
