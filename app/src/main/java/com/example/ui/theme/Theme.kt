package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DeepDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = Color(0xFF030712),
    primaryContainer = Color(0xFF0C2437),
    onPrimaryContainer = NeonCyan,
    secondary = NeonBlue,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFF1E293B),
    onSecondaryContainer = Color(0xFF93C5FD),
    tertiary = NeonEmerald,
    onTertiary = Color(0xFF022C22),
    tertiaryContainer = Color(0xFF064E3B),
    onTertiaryContainer = Color(0xFF6EE7B7),
    background = DarkBackground,
    onBackground = TextWhite,
    surface = DarkBackgroundElevated,
    onSurface = TextWhite,
    surfaceVariant = DarkSurfaceCard,
    onSurfaceVariant = TextLight,
    error = NeonCoral,
    onError = Color.White
)

@Composable
fun FPGlassHelperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DeepDarkColorScheme,
        typography = Typography,
        content = content
    )
}
