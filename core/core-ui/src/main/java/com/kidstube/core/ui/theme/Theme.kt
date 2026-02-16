package com.kidstube.core.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val KidsColors = lightColorScheme(
    primary = Color(0xFFFF3D00),        // Bright red-orange
    onPrimary = Color.White,
    primaryContainer = Color(0xFFFFE0B2), // Light orange
    onPrimaryContainer = Color(0xFFBF360C),
    secondary = Color(0xFF00BCD4),       // Cyan
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFB2EBF2),
    onSecondaryContainer = Color(0xFF006064),
    tertiary = Color(0xFFFFEB3B),        // Yellow
    onTertiary = Color(0xFF333333),
    tertiaryContainer = Color(0xFFFFF9C4),
    onTertiaryContainer = Color(0xFFF57F17),
    background = Color(0xFFFFFDE7),      // Warm cream
    onBackground = Color(0xFF212121),
    surface = Color(0xFFFFFDE7),
    onSurface = Color(0xFF212121),
    surfaceVariant = Color(0xFFFFE0B2),
    onSurfaceVariant = Color(0xFF5D4037),
    error = Color(0xFFD32F2F),
    onError = Color.White,
)

private val KidsTypography = Typography(
    headlineLarge = TextStyle(fontSize = 32.sp, fontWeight = FontWeight.Bold),
    headlineMedium = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold),
    titleLarge = TextStyle(fontSize = 24.sp, fontWeight = FontWeight.SemiBold),
    titleMedium = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.SemiBold),
    titleSmall = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.Medium),
    bodyLarge = TextStyle(fontSize = 18.sp),
    bodyMedium = TextStyle(fontSize = 16.sp),
    bodySmall = TextStyle(fontSize = 14.sp),
    labelLarge = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.Medium),
    labelMedium = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium),
    labelSmall = TextStyle(fontSize = 12.sp),
)

@Composable
fun KidsTubeTheme(
    darkTheme: Boolean = false,
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    // Always light theme, no dynamic color — consistent bright palette for toddlers
    MaterialTheme(
        colorScheme = KidsColors,
        typography = KidsTypography,
        content = content
    )
}
