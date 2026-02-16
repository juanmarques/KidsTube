package com.kidstube.core.ui.components

import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

private val bouncingColors = listOf(
    Color(0xFFFF3D00), // Red-orange
    Color(0xFF00BCD4), // Cyan
    Color(0xFFFFEB3B), // Yellow
    Color(0xFF4CAF50), // Green
    Color(0xFFE91E63), // Pink
)

@Composable
fun LoadingState(
    message: String = "",
    modifier: Modifier = Modifier
) {
    val transition = rememberInfiniteTransition(label = "bounce")

    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            bouncingColors.forEachIndexed { index, color ->
                val offsetY by transition.animateFloat(
                    initialValue = 0f,
                    targetValue = -30f,
                    animationSpec = infiniteRepeatable(
                        animation = tween(
                            durationMillis = 400,
                            delayMillis = index * 100,
                            easing = LinearEasing
                        ),
                        repeatMode = RepeatMode.Reverse
                    ),
                    label = "bounce_$index"
                )
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .offset(y = offsetY.dp)
                        .clip(CircleShape)
                        .background(color)
                )
            }
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Minimal error with retry — toddler will just tap
        if (onRetry != null) {
            TextButton(onClick = onRetry) {
                Text(
                    text = "Tap to retry",
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }
    }
}

@Composable
fun EmptyState(
    message: String = "",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        // Intentionally minimal — no text for toddlers
    }
}
