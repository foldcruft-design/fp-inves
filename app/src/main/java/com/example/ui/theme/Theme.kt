package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightGlassColorScheme = lightColorScheme(
    primary = GlassPrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = Color(0xFF075985),
    secondary = GlassSecondary,
    onSecondary = Color.White,
    secondaryContainer = Color(0xFFE0E7FF),
    onSecondaryContainer = Color(0xFF1E3A8A),
    tertiary = GlassAccentMint,
    onTertiary = Color.White,
    tertiaryContainer = Color(0xFFD1FAE5),
    onTertiaryContainer = Color(0xFF065F46),
    background = GlassBackgroundMid,
    onBackground = TextSlateDark,
    surface = Color.White,
    onSurface = TextSlateDark,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSlateMedium,
    error = GlassAccentDanger,
    onError = Color.White
)

@Composable
fun FPGlassHelperTheme(
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = LightGlassColorScheme,
        typography = Typography,
        content = content
    )
}
