package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val SophisticatedDarkColorScheme = darkColorScheme(
    primary = SophisticatedPrimary,
    onPrimary = SophisticatedOnPrimary,
    primaryContainer = SophisticatedPrimaryContainer,
    onPrimaryContainer = SophisticatedPrimary,
    secondary = SophisticatedSecondary,
    onSecondary = SophisticatedOnPrimary,
    secondaryContainer = SophisticatedCardBg,
    onSecondaryContainer = SophisticatedTextPrimary,
    tertiary = SophisticatedTertiary,
    onTertiary = SophisticatedQueenDark,
    background = SophisticatedDarkBackground,
    onBackground = SophisticatedTextPrimary,
    surface = SophisticatedSurface,
    onSurface = SophisticatedTextPrimary,
    surfaceVariant = SophisticatedCardBg,
    onSurfaceVariant = SophisticatedTextSecondary,
    outline = SophisticatedBorder,
    outlineVariant = SophisticatedBorderSubtle
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            window?.let {
                it.statusBarColor = SophisticatedDarkBackground.toArgb()
                it.navigationBarColor = SophisticatedDarkBackground.toArgb()
                WindowCompat.getInsetsController(it, view).isAppearanceLightStatusBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = SophisticatedDarkColorScheme,
        typography = Typography,
        content = content
    )
}
