package com.swiftai.app.ui.screens.aitools

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swiftai.app.domain.model.AITool
import com.swiftai.app.domain.model.AITools
import com.swiftai.app.ui.navigation.Screen
import com.swiftai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AIToolsScreen(
    navController: NavController,
    userTier: String = "free" // "free", "pro", or "max"
) {
    var selectedCategory by remember { mutableStateOf("All") }

    val categories = listOf("All", "Text", "Language", "Development", "Creative", "Analysis", "Voice", "Vision", "Audio")

    val filteredTools = if (selectedCategory == "All") {
        AITools.tools
    } else {
        AITools.getToolsByCategory(selectedCategory)
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "AI Tools",
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Explore AI capabilities",
                            style = MaterialTheme.typography.bodySmall,
                            color = TextSecondary
                        )
                    }
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = SurfaceDark
                )
            )
        },
        containerColor = BackgroundDark
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            // Category Filter
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                // Header with user tier badge
                item {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(20.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color.Transparent
                        )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .background(
                                    brush = Brush.horizontalGradient(
                                        colors = listOf(GradientStart, GradientMiddle, GradientEnd)
                                    ),
                                    shape = RoundedCornerShape(20.dp)
                                )
                                .padding(20.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column {
                                    Text(
                                        text = when(userTier) {
                                            "max" -> "👑 SwiftAI Max"
                                            "pro" -> "🚀 SwiftAI Pro"
                                            else -> "⚡ Free Plan"
                                        },
                                        style = MaterialTheme.typography.headlineSmall,
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold
                                    )
                                    Text(
                                        text = "${filteredTools.size} tools available",
                                        style = MaterialTheme.typography.bodyMedium,
                                        color = Color.White.copy(alpha = 0.8f)
                                    )
                                }

                                if (userTier == "free") {
                                    Button(
                                        onClick = { /* Navigate to upgrade */ },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = Color.White.copy(alpha = 0.2f)
                                        )
                                    ) {
                                        Text("Upgrade", color = Color.White)
                                    }
                                }
                            }
                        }
                    }
                }

                // Category chips
                item {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.take(4).forEach { category ->
                            FilterChip(
                                selected = selectedCategory == category,
                                onClick = { selectedCategory = category },
                                label = { Text(category) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = Purple,
                                    selectedLabelColor = Color.White
                                )
                            )
                        }
                    }
                }

                // Tools list
                items(filteredTools) { tool ->
                    AIToolCard(
                        tool = tool,
                        userTier = userTier,
                        onClick = {
                            if (canAccessTool(tool, userTier)) {
                                navController.navigate(Screen.AIToolDetail.createRoute(tool.id))
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun AIToolCard(
    tool: AITool,
    userTier: String,
    onClick: () -> Unit
) {
    val canAccess = canAccessTool(tool, userTier)

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = SurfaceVariantDark
        ),
        enabled = canAccess
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                // Icon
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .background(
                            color = when(tool.tier) {
                                "max" -> Amber.copy(alpha = 0.2f)
                                "pro" -> Purple.copy(alpha = 0.2f)
                                else -> Cyan.copy(alpha = 0.2f)
                            },
                            shape = RoundedCornerShape(12.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = tool.icon,
                        contentDescription = null,
                        tint = when(tool.tier) {
                            "max" -> Amber
                            "pro" -> Purple
                            else -> Cyan
                        },
                        modifier = Modifier.size(24.dp)
                    )
                }

                // Info
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Row(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = tool.name,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = if (canAccess) TextPrimary else TextSecondary
                        )

                        if (tool.isPremium) {
                            Surface(
                                shape = RoundedCornerShape(6.dp),
                                color = when(tool.tier) {
                                    "max" -> Amber
                                    "pro" -> Purple.copy(alpha = 0.8f)
                                    else -> Purple.copy(alpha = 0.5f)
                                }
                            ) {
                                Text(
                                    text = tool.tier.uppercase(),
                                    style = MaterialTheme.typography.labelSmall,
                                    color = if (tool.tier == "max") Color.Black else Color.White,
                                    fontWeight = FontWeight.Bold,
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))

                    Text(
                        text = tool.description,
                        style = MaterialTheme.typography.bodySmall,
                        color = TextSecondary
                    )
                }
            }

            // Lock icon if not accessible
            if (!canAccess) {
                Icon(
                    imageVector = Icons.Default.Lock,
                    contentDescription = "Locked",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

fun canAccessTool(tool: AITool, userTier: String): Boolean {
    return when (userTier) {
        "max" -> true // Max users can access everything
        "pro" -> tool.tier != "max" // Pro users can't access Max tools
        else -> !tool.isPremium // Free users can only access free tools
    }
}
