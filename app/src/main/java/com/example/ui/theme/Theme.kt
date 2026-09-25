package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val DarkColorScheme = darkColorScheme(
    primary = MedicalCyanAccent,
    onPrimary = MedicalNavy900,
    primaryContainer = MedicalBlueSecondary,
    onPrimaryContainer = Color.White,
    secondary = MedicalCyanDark,
    onSecondary = Color.White,
    background = MedicalNavy900,
    onBackground = TextPrimaryDark,
    surface = MedicalNavy800,
    onSurface = TextPrimaryDark,
    surfaceVariant = MedicalNavy700,
    onSurfaceVariant = TextSecondaryDark,
    outline = MedicalNavy600,
    error = ColorTumor,
    onError = Color.White
)

private val LightColorScheme = lightColorScheme(
    primary = MedicalBluePrimary,
    onPrimary = Color.White,
    primaryContainer = Color(0xFFE0F2FE),
    onPrimaryContainer = MedicalBlueSecondary,
    secondary = MedicalCyanDark,
    onSecondary = Color.White,
    background = MedicalLightBg,
    onBackground = TextPrimaryLight,
    surface = MedicalLightSurface,
    onSurface = TextPrimaryLight,
    surfaceVariant = Color(0xFFF1F5F9),
    onSurfaceVariant = TextSecondaryLight,
    outline = Color(0xFFCBD5E1),
    error = ColorTumor,
    onError = Color.White
)

@Composable
fun KidneyAiTheme(
    darkTheme: Boolean = true, // Default to high-tech dark clinical medical AI aesthetic
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
