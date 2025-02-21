package com.example.mybike.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun CircularScoreIndicator(
    score: Int,
    modifier: Modifier = Modifier,
    size: Float = 64f
) {
    // Simple traffic light colors
    val color = when {
        score >= 70 -> Color(0xFF4CAF50)  // Green
        score >= 40 -> Color(0xFFFFC107)  // Yellow
        else -> Color(0xFFF44336)         // Red
    }

    val textColor = when {
        score >= 40 -> Color.Black  // Dark text for green and yellow backgrounds
        else -> Color.White         // White text for red background
    }

    Box(
        modifier = modifier
            .size(size.dp)
            .clip(CircleShape)
            .background(color),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "${score}%",
                color = textColor,
                fontSize = (size / 3).sp,
                fontWeight = FontWeight.Bold
            )
            if (size >= 60) {  // Only show label if circle is big enough
                Text(
                    text = when {
                        score >= 70 -> "Good"
                        score >= 40 -> "Fair"
                        else -> "Poor"
                    },
                    color = textColor,
                    fontSize = (size / 5).sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
} 