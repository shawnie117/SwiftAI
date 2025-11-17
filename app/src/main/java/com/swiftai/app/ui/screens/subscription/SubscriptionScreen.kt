package com.swiftai.app.ui.screens.subscription

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.swiftai.app.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SubscriptionScreen(
    navController: NavController,
    currentPlan: String = "free" // "free", "pro", "max"
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Upgrade Plan",
                        fontWeight = FontWeight.Bold
                    )
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
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = Color.Transparent)
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
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "⚡",
                            style = MaterialTheme.typography.displayMedium
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "Choose Your Plan",
                            style = MaterialTheme.typography.headlineMedium,
                            color = Color.White,
                            fontWeight = FontWeight.Bold
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Unlock the full power of SwiftAI",
                            style = MaterialTheme.typography.bodyLarge,
                            color = Color.White.copy(alpha = 0.9f),
                            textAlign = TextAlign.Center
                        )
                    }
                }
            }

            // Free Plan
            PlanCard(
                title = "Free",
                price = "₹0",
                period = "forever",
                icon = "⚡",
                isCurrentPlan = currentPlan == "free",
                isPopular = false,
                features = listOf(
                    "SwiftAI Mini & Standard models",
                    "Basic text generation",
                    "Translation",
                    "Grammar checking",
                    "Text summarization",
                    "5 messages per day",
                    "Community support"
                ),
                onSubscribe = { /* Already free */ }
            )

            // Pro Plan
            PlanCard(
                title = "Pro",
                price = "₹299",
                period = "/month",
                icon = "🚀",
                isCurrentPlan = currentPlan == "pro",
                isPopular = true,
                features = listOf(
                    "Everything in Free",
                    "SwiftAI Pro model",
                    "Advanced text generation",
                    "Code assistant & review",
                    "Image generation prompts",
                    "Creative writing tools",
                    "Document analysis",
                    "Data analysis",
                    "100 messages per day",
                    "Priority support"
                ),
                onSubscribe = { /* TODO: Handle subscription */ }
            )

            // Max Plan
            PlanCard(
                title = "Max",
                price = "₹599",
                period = "/month",
                icon = "👑",
                isCurrentPlan = currentPlan == "max",
                isPopular = false,
                features = listOf(
                    "Everything in Pro",
                    "SwiftAI Max model",
                    "Text-to-speech",
                    "Speech-to-text",
                    "Vision tools",
                    "Audio enhancement",
                    "Video analysis",
                    "Advanced image generation",
                    "Unlimited messages",
                    "24/7 Premium support",
                    "Early access to new features",
                    "API access"
                ),
                onSubscribe = { /* TODO: Handle subscription */ }
            )

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}

@Composable
fun PlanCard(
    title: String,
    price: String,
    period: String,
    icon: String,
    isCurrentPlan: Boolean,
    isPopular: Boolean,
    features: List<String>,
    onSubscribe: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isPopular) Purple.copy(alpha = 0.1f) else SurfaceVariantDark
        ),
        border = if (isPopular) CardDefaults.outlinedCardBorder().copy(
            width = 2.dp,
            brush = Brush.horizontalGradient(listOf(Purple, Cyan))
        ) else null
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = icon,
                        style = MaterialTheme.typography.displaySmall
                    )
                    Column {
                        Text(
                            text = title,
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = TextPrimary
                        )
                        Row(
                            verticalAlignment = Alignment.Bottom,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = price,
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = Purple
                            )
                            Text(
                                text = period,
                                style = MaterialTheme.typography.bodyLarge,
                                color = TextSecondary
                            )
                        }
                    }
                }

                if (isPopular) {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = Amber
                    ) {
                        Text(
                            text = "POPULAR",
                            style = MaterialTheme.typography.labelSmall,
                            color = Color.Black,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Features
            features.forEach { feature ->
                Row(
                    modifier = Modifier.padding(vertical = 6.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.CheckCircle,
                        contentDescription = null,
                        tint = if (isPopular) Purple else Cyan,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = feature,
                        style = MaterialTheme.typography.bodyMedium,
                        color = TextPrimary
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Subscribe Button
            Button(
                onClick = onSubscribe,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                enabled = !isCurrentPlan,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isPopular) Purple else Cyan,
                    disabledContainerColor = TextSecondary.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(16.dp)
            ) {
                Text(
                    text = if (isCurrentPlan) "Current Plan" else "Subscribe to $title",
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
