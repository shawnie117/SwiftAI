// app/src/main/java/com/swiftai/app/ui/components/SuggestionChips.kt

package com.swiftai.app.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SuggestionChips(
    suggestions: List<String>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
    ) {
        suggestions.forEach { text ->
            Surface(
                onClick = { onSuggestionClick(text) },
                shape = RoundedCornerShape(20.dp),
                color = Color(0xFF1E2128),
                modifier = Modifier.widthIn(min = 180.dp)
            ) {
                Text(
                    text = text,
                    fontSize = 15.sp,
                    color = Color(0xFFB0B3BA),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }
    }
}

