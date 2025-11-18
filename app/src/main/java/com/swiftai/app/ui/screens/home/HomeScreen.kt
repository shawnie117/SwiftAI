package com.swiftai.app.ui.screens.home

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftai.app.ui.navigation.Screen
import com.swiftai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavController,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    // Log user ID for debugging
    LaunchedEffect(Unit) {
        val userId = viewModel.getUserId()
        Log.d("HomeScreen", "User ID: $userId")
        Log.d("HomeScreen", "Current chats count: ${uiState.chats.size}")
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // SwiftAI Logo
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .background(
                                    brush = Brush.linearGradient(
                                        colors = listOf(Purple, Cyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "⚡",
                                style = MaterialTheme.typography.titleLarge
                            )
                        }

                        Column {
                            Text(
                                text = "SwiftAI",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = TextPrimary
                            )

                            // Subscription badge
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = when(uiState.userTier) {
                                    "pro" -> Purple.copy(alpha = 0.2f)
                                    "max" -> Amber.copy(alpha = 0.2f)
                                    else -> TextSecondary.copy(alpha = 0.1f)
                                }
                            ) {
                                Text(
                                    text = when(uiState.userTier) {
                                        "pro" -> "PRO"
                                        "max" -> "MAX"
                                        else -> "FREE"
                                    },
                                    style = MaterialTheme.typography.labelSmall,
                                    fontWeight = FontWeight.Bold,
                                    color = when(uiState.userTier) {
                                        "pro" -> Purple
                                        "max" -> Amber
                                        else -> TextSecondary
                                    },
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }
                },
                actions = {
                    IconButton(onClick = { viewModel.refresh() }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Refresh",
                            tint = TextPrimary
                        )
                    }
                    IconButton(onClick = { navController.navigate(Screen.Settings.route) }) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    val newChatId = viewModel.createNewChat()
                    navController.navigate(Screen.Chat.createRoute(newChatId))
                },
                containerColor = Purple,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "New Chat",
                    tint = TextPrimary
                )
            }
        }
    ) { paddingValues ->
        // Show loading state
        if (uiState.isLoading && uiState.chats.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Purple)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(horizontal = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
            item { Spacer(modifier = Modifier.height(8.dp)) }

            // Search Bar
            item {
                OutlinedTextField(
                    value = uiState.searchQuery,
                    onValueChange = { viewModel.onSearchQueryChanged(it) },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = {
                        Text(
                            text = "Search chats...",
                            color = TextSecondary
                        )
                    },
                    leadingIcon = {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = "Search",
                            tint = TextSecondary
                        )
                    },
                    trailingIcon = {
                        if (uiState.searchQuery.isNotEmpty()) {
                            IconButton(onClick = { viewModel.onSearchQueryChanged("") }) {
                                Icon(
                                    imageVector = Icons.Default.Close,
                                    contentDescription = "Clear",
                                    tint = TextSecondary
                                )
                            }
                        }
                    },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        focusedBorderColor = Purple,
                        unfocusedBorderColor = SurfaceVariantDark,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Purple
                    ),
                    shape = RoundedCornerShape(16.dp),
                    singleLine = true
                )
            }

            // Browse AI Tools Card
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable {
                            if (uiState.userTier == "free") {
                                // Show upgrade prompt
                                navController.navigate(Screen.Subscription.route)
                            } else {
                                navController.navigate(Screen.AITools.route)
                            }
                        },
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = SurfaceVariantDark
                    )
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .background(
                                brush = Brush.horizontalGradient(
                                    colors = listOf(
                                        Purple.copy(alpha = 0.3f),
                                        Cyan.copy(alpha = 0.3f)
                                    )
                                )
                            )
                            .padding(20.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = "🎨 Browse AI Tools",
                                    style = MaterialTheme.typography.titleLarge,
                                    fontWeight = FontWeight.Bold,
                                    color = TextPrimary
                                )
                                Spacer(modifier = Modifier.height(4.dp))
                                Text(
                                    text = if (uiState.userTier == "free") {
                                        "Unlock powerful AI tools with Pro"
                                    } else {
                                        "Image generation, code help & more"
                                    },
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = TextSecondary
                                )
                            }

                            Icon(
                                imageVector = if (uiState.userTier == "free") {
                                    Icons.Default.Lock
                                } else {
                                    Icons.AutoMirrored.Filled.ArrowForward
                                },
                                contentDescription = null,
                                tint = if (uiState.userTier == "free") Amber else Purple,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                    }
                }
            }

            // Pinned Tools Section (Only for paid users)
            if (uiState.userTier != "free" && uiState.pinnedTools.isNotEmpty()) {
                item {
                    Text(
                        text = "📌 Pinned Tools",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )
                }

                items(
                    items = uiState.pinnedTools,
                    key = { toolId -> toolId }
                ) { toolId ->
                    PinnedToolCard(
                        toolId = toolId,
                        onClick = {
                            navController.navigate(Screen.AIToolDetail.createRoute(toolId))
                        },
                        onUnpin = {
                            viewModel.unpinTool(toolId)
                        }
                    )
                }
            }

            // Section Header
            item {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Recent Chats",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = TextPrimary
                    )

                    if (uiState.chats.isNotEmpty()) {
                        TextButton(onClick = { /* TODO: Show all */ }) {
                            Text("See all", color = Purple)
                        }
                    }
                }
            }

            // Chat List
            if (uiState.chats.isEmpty()) {
                item {
                    EmptyState()
                }
            } else {
                items(
                    items = uiState.chats,
                    key = { chat -> chat.id }
                ) { chat ->
                    ChatCard(
                        chat = chat,
                        onClick = {
                            navController.navigate(Screen.Chat.createRoute(chat.id))
                        },
                        onDelete = {
                            viewModel.deleteChat(chat.id)
                        }
                    )
                }
            }

            item { Spacer(modifier = Modifier.height(80.dp)) }
            }
        }
    }
}

@Composable
fun PinnedToolCard(
    toolId: String,
    onClick: () -> Unit,
    onUnpin: () -> Unit
) {
    // Tool data would come from AITools object
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(40.dp)
                        .background(Purple.copy(alpha = 0.2f), RoundedCornerShape(10.dp)),
                    contentAlignment = Alignment.Center
                ) {
                    Text("🎨", style = MaterialTheme.typography.titleMedium)
                }

                Column {
                    Text(
                        text = toolId.replace("_", " ").replaceFirstChar { it.uppercase() },
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium,
                        color = TextPrimary
                    )
                    Text(
                        text = "Tap to use",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            IconButton(onClick = onUnpin) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Unpin",
                    tint = TextSecondary
                )
            }
        }
    }
}

@Composable
fun ChatCard(
    chat: com.swiftai.app.domain.model.Chat,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantDark
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = chat.title,
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Medium,
                    color = TextPrimary,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = formatTimestamp(chat.lastMessageTime),
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary
                )
            }

            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "More",
                        tint = TextSecondary
                    )
                }

                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("Delete") },
                        onClick = {
                            showMenu = false
                            onDelete()
                        },
                        leadingIcon = {
                            Icon(Icons.Default.Delete, contentDescription = null)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyState() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 60.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "💬",
            style = MaterialTheme.typography.displayLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = "No chats yet",
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.Bold,
            color = TextPrimary
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = "Start a conversation with SwiftAI",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )
    }
}

fun formatTimestamp(timestamp: Long): String {
    val now = System.currentTimeMillis()
    val diff = now - timestamp

    return when {
        diff < 60_000 -> "Just now"
        diff < 3600_000 -> "${diff / 60_000}m ago"
        diff < 86400_000 -> "${diff / 3600_000}h ago"
        diff < 604800_000 -> "${diff / 86400_000}d ago"
        else -> "${diff / 604800_000}w ago"
    }
}
