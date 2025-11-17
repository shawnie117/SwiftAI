package com.swiftai.app.ui.screens.aitools

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.swiftai.app.ui.navigation.Screen
import com.swiftai.app.ui.theme.*

data class AITool(
    val id: String,
    val name: String,
    val description: String,
    val icon: String,
    val category: String,
    val isPinned: Boolean = false
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolsScreen(
    navController: NavController,
    viewModel: AIToolsViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val tools = listOf(
        AITool("image_gen", "Image Generator", "Create stunning AI images", "🎨", "creative"),
        AITool("code_helper", "Code Assistant", "AI-powered coding help", "💻", "development"),
        AITool("writer", "AI Writer", "Generate quality content", "✍️", "creative"),
        AITool("translator", "Translator", "50+ languages support", "🌐", "utility"),
        AITool("summarizer", "Summarizer", "Condense long content", "📝", "utility"),
        AITool("data_analyst", "Data Analyst", "Analyze your data", "📊", "business"),
        AITool("voice_gen", "Voice Generator", "Text to speech AI", "🎤", "creative"),
        AITool("video_editor", "Video Editor", "AI video enhancement", "🎥", "creative")
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AI Tools",
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(16.dp),
            horizontalArrangement = Arrangement.spacedBy(12.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(tools) { tool ->
                AIToolCard(
                    tool = tool,
                    isPinned = uiState.pinnedTools.contains(tool.id),
                    onClick = {
                        navController.navigate(Screen.AIToolDetail.createRoute(tool.id))
                    },
                    onPinToggle = {
                        viewModel.togglePin(tool.id)
                    }
                )
            }
        }
    }
}

@Composable
fun AIToolCard(
    tool: AITool,
    isPinned: Boolean,
    onClick: () -> Unit,
    onPinToggle: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .aspectRatio(1f)
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantDark
        )
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(RoundedCornerShape(16.dp))
                        .background(Purple.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = tool.icon,
                        style = MaterialTheme.typography.headlineMedium
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = tool.name,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = tool.description,
                    style = MaterialTheme.typography.bodySmall,
                    color = TextSecondary,
                    maxLines = 2
                )
            }

            // Pin button
            IconButton(
                onClick = onPinToggle,
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp)
            ) {
                Icon(
                    imageVector = if (isPinned) Icons.Default.PushPin else Icons.Default.PushPin,
                    contentDescription = "Pin",
                    tint = if (isPinned) Amber else TextSecondary.copy(alpha = 0.5f)
                )
            }
        }
    }
}
