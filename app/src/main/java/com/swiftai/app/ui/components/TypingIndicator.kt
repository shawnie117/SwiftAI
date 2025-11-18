package com.swiftai.app.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

// Colors matching MessageBubble
private val AIBubbleBackground = Color(0xFF232332)
private val DotColor = Color(0xFFDEE2F1)
private val AvatarBackground = Color(0xFF6C63FF)

@Composable
fun TypingIndicator(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "typing")

    // Animated dots with bouncing effect
    val dot1Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot1"
    )

    val dot2Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot2"
    )

    val dot3Scale by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, delayMillis = 400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot3"
    )

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.Start
    ) {
        // AI avatar (matching MessageBubble)
        Box(
            modifier = Modifier
                .size(26.dp)
                .clip(CircleShape)
                .background(AvatarBackground),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Bolt,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(16.dp)
            )
        }

        Spacer(modifier = Modifier.width(6.dp))

        // Typing bubble
        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 4.dp,
                        topEnd = 18.dp,
                        bottomStart = 18.dp,
                        bottomEnd = 18.dp
                    )
                )
                .background(AIBubbleBackground)
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                horizontalArrangement = Arrangement.spacedBy(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Animated dots
                Box(
                    modifier = Modifier
                        .size(8.dp * dot1Scale)
                        .clip(CircleShape)
                        .background(DotColor)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp * dot2Scale)
                        .clip(CircleShape)
                        .background(DotColor)
                )
                Box(
                    modifier = Modifier
                        .size(8.dp * dot3Scale)
                        .clip(CircleShape)
                        .background(DotColor)
                )
            }
        }
    }
}

