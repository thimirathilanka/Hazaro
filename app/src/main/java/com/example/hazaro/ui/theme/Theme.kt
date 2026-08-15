package com.example.hazaro.ui.theme

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val LightColorScheme = lightColorScheme(
    primary = Teal,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFD4EDEC),
    onPrimaryContainer = TealDeep,
    secondary = Amber,
    onSecondary = Ink,
    secondaryContainer = AmberContainer,
    onSecondaryContainer = Color(0xFF3F2E00),
    tertiary = Color(0xFF3D5A80),
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE1EBE9),
    onSurfaceVariant = Color(0xFF3E4F50),
    error = ErrorRed,
    outline = Color(0xFF6F7F80),
)

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = TealDeep,
    primaryContainer = Teal,
    onPrimaryContainer = Color.White,
    secondary = Amber,
    onSecondary = Night,
    secondaryContainer = Color(0xFF5C4300),
    onSecondaryContainer = AmberContainer,
    tertiary = Color(0xFF98C1D9),
    background = Night,
    onBackground = Color(0xFFE6EEED),
    surface = NightSurface,
    onSurface = Color(0xFFE6EEED),
    surfaceVariant = Color(0xFF243536),
    onSurfaceVariant = Color(0xFFC5D4D4),
    error = Color(0xFFFFB4AB),
    outline = Color(0xFF8A9A9B),
)

@Composable
fun HazaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    MaterialTheme(
        colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme,
        typography = Typography,
        content = content,
    )
}
