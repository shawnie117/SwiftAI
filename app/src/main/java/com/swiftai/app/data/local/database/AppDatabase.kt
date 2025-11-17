package com.swiftai.app.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.swiftai.app.data.local.dao.ChatDao
import com.swiftai.app.data.local.dao.MessageDao
import com.swiftai.app.data.local.dao.UserDao
import com.swiftai.app.data.local.entity.ChatEntity
import com.swiftai.app.data.local.entity.MessageEntity
import com.swiftai.app.data.local.entity.UserEntity

@Database(
    entities = [UserEntity::class, ChatEntity::class, MessageEntity::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun chatDao(): ChatDao
    abstract fun messageDao(): MessageDao
}
