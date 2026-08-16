package com.example.hazaro.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

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
    onTertiary = Color.White,
    background = Cream,
    onBackground = Ink,
    surface = Color.White,
    onSurface = Ink,
    surfaceVariant = Color(0xFFE1EBE9),
    onSurfaceVariant = Color(0xFF3E4F50),
    surfaceTint = Teal,
    inverseSurface = Night,
    inverseOnSurface = Color(0xFFE6EEED),
    error = ErrorRed,
    onError = Color.White,
    outline = Color(0xFF6F7F80),
    outlineVariant = Color(0xFFC5D4D3),
)

private val DarkColorScheme = darkColorScheme(
    primary = TealLight,
    onPrimary = Color(0xFF003738),
    primaryContainer = Color(0xFF0E4F50),
    onPrimaryContainer = Color(0xFFB6E6E7),
    secondary = AmberDark,
    onSecondary = Color(0xFF2A1A00),
    secondaryContainer = Color(0xFF5C4300),
    onSecondaryContainer = Color(0xFFFFE0A3),
    tertiary = Color(0xFF98C1D9),
    onTertiary = Color(0xFF00344A),
    background = Night,
    onBackground = Color(0xFFDDE7E6),
    surface = NightSurface,
    onSurface = Color(0xFFDDE7E6),
    surfaceVariant = Color(0xFF2A3C3D),
    onSurfaceVariant = Color(0xFFB7C8C8),
    surfaceTint = TealLight,
    inverseSurface = Color(0xFFDDE7E6),
    inverseOnSurface = Night,
    error = Color(0xFFFFB4AB),
    onError = Color(0xFF690005),
    outline = Color(0xFF809191),
    outlineVariant = Color(0xFF3A4C4D),
    scrim = Color(0xFF000000),
)

@Composable
fun HazaroTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit,
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            val insetsController = WindowCompat.getInsetsController(window, view)
            insetsController.isAppearanceLightStatusBars = !darkTheme
            insetsController.isAppearanceLightNavigationBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content,
    )
}
