package com.trailbook.core.design.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val TrailGreen = Color(0xFF2D6A4F)
private val TrailGreenLight = Color(0xFF40916C)
private val TrailSand = Color(0xFFF8F5F0)
private val TrailDark = Color(0xFF1B1B1F)

private val LightColors = lightColorScheme(
    primary = TrailGreen,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFB7E4C7),
    onPrimaryContainer = Color(0xFF1B4332),
    secondary = Color(0xFF52796F),
    background = TrailSand,
    surface = Color.White,
    onBackground = Color(0xFF1C1B1F),
    onSurface = Color(0xFF1C1B1F),
    surfaceVariant = Color(0xFFE8E4DF),
    outline = Color(0xFF79747E)
)

private val DarkColors = darkColorScheme(
    primary = TrailGreenLight,
    onPrimary = Color(0xFF003822),
    primaryContainer = Color(0xFF1B4332),
    onPrimaryContainer = Color(0xFFB7E4C7),
    secondary = Color(0xFFB5CCB9),
    background = TrailDark,
    surface = Color(0xFF252529),
    onBackground = Color(0xFFE6E1E5),
    onSurface = Color(0xFFE6E1E5),
    surfaceVariant = Color(0xFF3A3A3F),
    outline = Color(0xFF938F99)
)

@Composable
fun TrailBookTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColors else LightColors,
        typography = TrailBookTypography,
        content = content
    )
}
