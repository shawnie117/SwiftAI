// app/src/main/java/com/swiftai/app/ui/screens/chat/ChatScreen.kt

package com.swiftai.app.ui.screens.chat

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftai.app.domain.model.Message
import com.swiftai.app.ui.components.MessageBubble
import com.swiftai.app.ui.components.SuggestionChips
import com.swiftai.app.ui.components.TypingIndicator
import kotlinx.coroutines.launch

// Enhanced Color Palette
private val UserBubble = Color(0xFF6C63FF)
private val UserBubbleGradientStart = Color(0xFF6C63FF)
private val UserBubbleGradientEnd = Color(0xFF8249FF)
private val AIBubble = Color(0xFF1E2128)
private val AIBubbleSecondary = Color(0xFF262D34)
private val Background = Color(0xFF0F1115)
private val SurfaceDark = Color(0xFF17191D)
private val SurfaceVariantDark = Color(0xFF1E2128)
private val Purple = Color(0xFF8249FF)
private val PurpleLight = Color(0xFFB794FF)
private val Cyan = Color(0xFF09DAC6)
private val Amber = Color(0xFFFFB84D)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFFB0B3BA)
private val DividerColor = Color(0xFF2A2D35)

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatId) {
        viewModel.loadChat(chatId)
    }

    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch {
                listState.animateScrollToItem(uiState.messages.size - 1)
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        // AI Avatar with gradient glow
                        Box(
                            modifier = Modifier
                                .size(40.dp)
                                .clip(CircleShape)
                                .background(
                                    Brush.linearGradient(
                                        colors = listOf(Purple, Cyan)
                                    )
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                "⚡",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        Column {
                            Text(
                                "SwiftAI",
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp,
                                color = TextPrimary
                            )
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                // Status indicator dot
                                Box(
                                    modifier = Modifier
                                        .size(8.dp)
                                        .clip(CircleShape)
                                        .background(
                                            if (uiState.isLoading) Amber else Cyan
                                        )
                                )
                                Text(
                                    if (uiState.isLoading) "Thinking..." else "Online",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Normal,
                                    color = if (uiState.isLoading) Amber else Cyan
                                )
                            }
                        }
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            Icons.AutoMirrored.Filled.ArrowBack,
                            "Back",
                            tint = TextPrimary
                        )
                    }
                },
                actions = {
                    IconButton(onClick = { /* TODO: More options */ }) {
                        Icon(
                            Icons.Default.MoreVert,
                            "More",
                            tint = TextPrimary
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = Background
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .imePadding()
        ) {
            // Divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerColor)
            )

            // Messages area
            Box(modifier = Modifier.weight(1f)) {
                when {
                    // Show loading spinner when initially loading messages
                    uiState.isLoading && uiState.messages.isEmpty() -> {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            CircularProgressIndicator(color = Purple)
                        }
                    }
                    // Show empty state only when truly no messages (not loading)
                    uiState.messages.isEmpty() && !uiState.isLoading -> {
                        EmptyChatState(
                            onSuggestionClick = { suggestion ->
                                viewModel.updateInputText(suggestion)
                            }
                        )
                    }
                    // Show messages
                    else -> {
                        LazyColumn(
                            state = listState,
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(
                                top = 8.dp,
                                bottom = 100.dp
                            ),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(
                                items = uiState.messages,
                                key = { it.id }
                            ) { message ->
                                MessageBubble(
                                    message = message,
                                    isUser = message.isUser,
                                    modifier = Modifier.animateItemPlacement(
                                        animationSpec = tween(
                                            durationMillis = 300,
                                            easing = FastOutSlowInEasing
                                        )
                                    )
                                )
                            }

                            if (uiState.isThinking) {
                                item { TypingIndicator() }
                            }
                        }
                    }
                }
            }

            // Input area
            MessageInput(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                onSend = { viewModel.sendMessage() },
                enabled = !uiState.isThinking // disable only while thinking
            )
        }
    }
}

@Composable
fun EmptyChatState(
    onSuggestionClick: (String) -> Unit
) {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // ... (logo and greetings above)

        SuggestionChips(
            suggestions = listOf(
                "💡 Help me brainstorm",
                "📝 Write something",
                "🎨 Get creative"
            ),
            onSuggestionClick = onSuggestionClick
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageInput(
    value: String,
    onValueChange: (String) -> Unit,
    onSend: () -> Unit,
    enabled: Boolean
) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shadowElevation = 12.dp
    ) {
        Column {
            // Top divider
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(1.dp)
                    .background(DividerColor)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Text input
                TextField(
                    value = value,
                    onValueChange = onValueChange,
                    placeholder = {
                        Text(
                            "Message SwiftAI...",
                            color = TextSecondary
                        )
                    },
                    shape = RoundedCornerShape(24.dp),
                    modifier = Modifier.weight(1f),
                    enabled = enabled,
                    colors = TextFieldDefaults.colors(
                        focusedContainerColor = SurfaceVariantDark,
                        unfocusedContainerColor = SurfaceVariantDark,
                        disabledContainerColor = SurfaceVariantDark,
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent,
                        disabledIndicatorColor = Color.Transparent,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary,
                        cursorColor = Purple
                    ),
                    maxLines = 5
                )

                // Send button with gradient
                FloatingActionButton(
                    onClick = {
                        if (value.isNotBlank()) onSend()
                    },
                    containerColor = if (value.isNotBlank()) Purple else SurfaceVariantDark,
                    modifier = Modifier.size(52.dp),
                    shape = CircleShape,
                    elevation = FloatingActionButtonDefaults.elevation(
                        defaultElevation = 0.dp,
                        pressedElevation = 4.dp
                    )
                ) {
                    Icon(
                        Icons.AutoMirrored.Filled.Send,
                        "Send",
                        tint = if (value.isNotBlank()) TextPrimary else TextSecondary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}
