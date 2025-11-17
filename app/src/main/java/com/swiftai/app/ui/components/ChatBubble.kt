package com.swiftai.app.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

@Composable
fun ChatBubble(text: String, isUser: Boolean) {
    Row(
        Modifier
            .fillMaxWidth()
            .padding(3.dp),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Box(
            Modifier
                .background(
                    if (isUser) Color(0xFF1976D2) else Color(0xFF262D34),
                    shape = RoundedCornerShape(18.dp)
                )
                .padding(horizontal = 14.dp, vertical = 10.dp)
                .widthIn(max = 300.dp)
        ) {
            Text(
                text = text,
                color = if (isUser) Color.White else Color(0xFFD3E3FD)
            )
        }
    }
}
