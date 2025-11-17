package com.swiftai.app.ui.screens.chat

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Divider
import androidx.compose.material3.ListItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.swiftai.app.domain.model.Chat

@Composable
fun ChatListScreen(
    chats: List<Chat>,
    onChatClick: (Chat) -> Unit,
    onNewChat: () -> Unit
) {
    var query by remember { mutableStateOf("") }
    Column(Modifier.fillMaxSize()) {
        OutlinedTextField(
            value = query,
            onValueChange = { query = it },
            label = { Text("Search chats") },
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        LazyColumn(Modifier.weight(1f)) {
            items(chats.filter { it.title.contains(query, ignoreCase = true) }) { chat ->
                ListItem(
                    headlineContent = { Text(chat.title) },
                    supportingContent = { Text(chat.lastMessageTime.toString()) },
                    modifier = Modifier.clickable { onChatClick(chat) }
                )
                Divider()
            }
        }
    }
}
