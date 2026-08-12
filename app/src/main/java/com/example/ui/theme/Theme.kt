package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = CyanAccent,
    onPrimary = Color.Black,
    primaryContainer = TacticalSurfaceVariant,
    onPrimaryContainer = CyanAccent,
    secondary = SafetyAmber,
    onSecondary = Color.Black,
    secondaryContainer = TacticalSurfaceVariant,
    onSecondaryContainer = SafetyAmber,
    tertiary = StatusGreen,
    onTertiary = Color.Black,
    background = TacticalDarkBg,
    onBackground = TextPrimary,
    surface = TacticalSurface,
    onSurface = TextPrimary,
    surfaceVariant = TacticalSurfaceVariant,
    onSurfaceVariant = TextSecondary,
    outline = TacticalBorder,
    error = LiveRed,
    onError = Color.White
)

@Composable
fun ServiceCamTheme(
    darkTheme: Boolean = true, // Body cam defaults to dark tactical theme
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography = Typography,
        content = content
    )
}

