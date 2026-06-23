package com.example.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val DarkColorScheme = darkColorScheme(
    primary = BentoBlueDark,
    secondary = BentoAmberDark,
    tertiary = BentoGreenDark,
    background = BentoBackgroundDark,
    surface = BentoSurfaceDark,
    primaryContainer = Color(0xFF1E293B),
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(0xFF78350F),
    onSecondaryContainer = Color(0xFFFEF3C7),
    onPrimary = Color(0xFF0F172A),
    onSecondary = Color(0xFF0F172A),
    onBackground = Color(0xFFF1F5F9),
    onSurface = Color(0xFFF1F5F9),
    onSurfaceVariant = Color(0xFF94A3B8),
    surfaceVariant = BentoBorderDark,
    error = RedAccent
)

private val LightColorScheme = lightColorScheme(
    primary = BentoBlue,
    secondary = BentoAmber,
    tertiary = BentoGreen,
    background = BentoBackgroundLight,
    surface = BentoSurfaceLight,
    primaryContainer = Color(0xFF0F172A), // High contrast slate-900 container for Bento highlights in light mode
    onPrimaryContainer = Color.White,
    secondaryContainer = Color(0xFFFEF3C7), // Amber 100
    onSecondaryContainer = Color(0xFF78350F), // Amber 900
    onPrimary = Color.White,
    onSecondary = Color.White,
    onBackground = Color(0xFF0F172A),
    onSurface = Color(0xFF0F172A),
    onSurfaceVariant = Color(0xFF64748B),
    surfaceVariant = BentoBorderLight,
    error = RedAccent
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
