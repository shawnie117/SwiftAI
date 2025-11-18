// app/src/main/java/com/swiftai/app/ui/components/MessageBubble.kt

package com.swiftai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.swiftai.app.domain.model.Message

// Color definitions
private val UserBubbleGradient = Brush.linearGradient(
    listOf(Color(0xFF6C63FF), Color(0xFF8249FF))
)
private val AIBubbleBackground = Color(0xFF232332)
private val AIBubbleText = Color(0xFFDEE2F1)
private val UserBubbleText = Color.White
private val AvatarBackground = Color(0xFF6C63FF)

@Composable
fun MessageBubble(
    message: Message,
    isUser: Boolean,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        // AI avatar (left side, only for AI messages)
        if (!isUser) {
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
        }

        // Message bubble
        Surface(
            modifier = Modifier.widthIn(max = 280.dp),
            shape = RoundedCornerShape(
                topStart = if (isUser) 18.dp else 4.dp,
                topEnd = if (isUser) 4.dp else 18.dp,
                bottomStart = 18.dp,
                bottomEnd = 18.dp
            ),
            color = Color.Transparent
        ) {
            val bubbleModifier = if (isUser) {
                Modifier.background(UserBubbleGradient)
            } else {
                Modifier.background(AIBubbleBackground)
            }

            Box(modifier = bubbleModifier) {
                // Format the text content - replace ** with bold markers, handle line breaks
                val formattedText = formatMessageText(message.content)

                Text(
                    text = formattedText,
                    color = if (isUser) UserBubbleText else AIBubbleText,
                    fontSize = 15.sp,
                    fontWeight = if (isUser) FontWeight.Bold else FontWeight.Normal,
                    modifier = Modifier.padding(
                        horizontal = 12.dp,
                        vertical = 10.dp
                    ),
                    lineHeight = 20.sp // Better line spacing
                )
            }
        }
    }
}

// Helper function to format message text
private fun formatMessageText(text: String): String {
    // Replace **text** with just text (remove markdown bold markers)
    // and ensure proper line breaks
    return text
        .replace("**", "") // Remove bold markers
        .replace("\\n", "\n") // Handle escaped newlines
        .trim()
}

