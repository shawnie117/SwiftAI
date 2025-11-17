package com.swiftai.app.ui.screens.chat

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
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftai.app.domain.model.Message
import kotlinx.coroutines.launch

// Color definitions — update to match your theme
private val UserBubble = Color(0xFF1976D2)
private val AIBubble = Color(0xFF262D34)
private val Background = Color(0xFF17191D)
private val SurfaceDark = Color(0xFF1B1E23)
private val SurfaceVariantDark = Color(0xFF23272B)
private val Purple = Color(0xFF8249FF)
private val Cyan = Color(0xFF09DAC6)
private val Amber = Color(0xFFFFB300)
private val TextPrimary = Color.White
private val TextSecondary = Color(0xFFBFC1C6)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChatScreen(
    chatId: String,
    navController: NavController,
    viewModel: ChatViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    LaunchedEffect(chatId) { viewModel.loadChat(chatId) }
    LaunchedEffect(uiState.messages.size) {
        if (uiState.messages.isNotEmpty()) {
            scope.launch { listState.animateScrollToItem(uiState.messages.size - 1) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(Brush.linearGradient(listOf(Purple, Cyan))),
                        contentAlignment = Alignment.Center
                    ) { Text("⚡", fontWeight = FontWeight.Bold) }

                    Column {
                        Text("SwiftAI", fontWeight = FontWeight.Bold)
                        Text(
                            if (uiState.isLoading) "Thinking..." else "Online",
                            fontWeight = FontWeight.Normal,
                            color = if (uiState.isLoading) Amber else Cyan
                        )
                    }
                }},
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back", tint = TextPrimary)
                    }
                },
                actions = {
                    IconButton(onClick = { /*TODO: Share*/ }) {
                        Icon(Icons.Default.Share, "Share", tint = TextPrimary)
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = SurfaceDark)
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
            Box(modifier = Modifier.weight(1f)) {
                if (uiState.messages.isEmpty()) {
                    EmptyChatState()
                } else {
                    LazyColumn(
                        state = listState,
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(16.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(uiState.messages) { message ->
                            MessageBubble(message)
                        }
                        if (uiState.isLoading) item { TypingIndicator() }
                        item { Spacer(Modifier.height(80.dp)) }
                    }
                }
            }
            MessageInput(
                value = uiState.inputText,
                onValueChange = { viewModel.updateInputText(it) },
                onSend = {
                    viewModel.sendMessage()
                },
                enabled = !uiState.isLoading
            )
        }
    }
}

@Composable
fun MessageBubble(message: Message) {
    val isUser = message.role == "user"

    Row(
        Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        if (!isUser) {
            Box(
                modifier = Modifier
                    .size(32.dp)
                    .clip(CircleShape)
                    .background(Purple.copy(alpha = 0.18f)),
                contentAlignment = Alignment.Center
            ) { Text("⚡") }
            Spacer(Modifier.width(8.dp))
        }
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = 16.dp, topEnd = 16.dp,
                bottomStart = if (isUser) 16.dp else 4.dp,
                bottomEnd = if (isUser) 4.dp else 16.dp
            ),
            color = if (isUser) UserBubble else AIBubble
        ) {
            Text(
                text = message.content,
                color = TextPrimary,
                modifier = Modifier.padding(14.dp)
            )
        }
    }
}

@Composable
fun TypingIndicator() {
    Row(verticalAlignment = Alignment.CenterVertically) {
        Box(
            Modifier
                .size(32.dp)
                .clip(CircleShape)
                .background(Purple.copy(alpha = 0.18f)),
            contentAlignment = Alignment.Center
        ) {
            Text("⚡")
        }
        Spacer(Modifier.width(8.dp))
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = SurfaceVariantDark
        ) {
            Row(
                Modifier.padding(16.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                repeat(3) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(TextSecondary)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyChatState() {
    Column(
        Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("👋", style = MaterialTheme.typography.displayLarge)
        Spacer(Modifier.height(16.dp))
        Text("Hi! I'm SwiftAI", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Spacer(Modifier.height(8.dp))
        Text("Ask me anything!", style = MaterialTheme.typography.bodyLarge, color = TextSecondary)
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
        Modifier.fillMaxWidth(),
        color = SurfaceDark,
        shadowElevation = 8.dp
    ) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.Bottom
        ) {
            TextField(
                value = value,
                onValueChange = onValueChange,
                placeholder = { Text("Message SwiftAI...", color = TextSecondary) },
                shape = RoundedCornerShape(22.dp),
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
                ),
                maxLines = 4
            )
            FloatingActionButton(
                onClick = onSend,
                containerColor = Purple,
                modifier = Modifier.size(48.dp),
                shape = CircleShape,
                elevation = FloatingActionButtonDefaults.elevation(0.dp, 2.dp)
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, "Send", tint = TextPrimary)
            }
        }
    }
}
