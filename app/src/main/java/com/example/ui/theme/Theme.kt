package com.example.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val CyberpunkDarkColorScheme = darkColorScheme(
    primary = NeonCyan,
    onPrimary = MidnightNavy,
    primaryContainer = TranslucentCharcoalElevated,
    onPrimaryContainer = NeonCyan,
    secondary = ElectricPurple,
    onSecondary = MidnightNavy,
    secondaryContainer = VividViolet.copy(alpha = 0.2f),
    onSecondaryContainer = ElectricPurple,
    tertiary = GlowingEmerald,
    onTertiary = MidnightNavy,
    background = MidnightNavy,
    onBackground = TextPrimary,
    surface = TranslucentCharcoal,
    onSurface = TextPrimary,
    surfaceVariant = TranslucentCharcoalElevated,
    onSurfaceVariant = TextSecondary,
    outline = CardBorderGlow,
    outlineVariant = CardBorderSubtle,
    error = NeonPink,
    onError = Color.White
)

@Composable
fun MediaVaultTheme(
    content: @Composable () -> Unit
) {
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as? Activity)?.window
            if (window != null) {
                window.statusBarColor = MidnightNavy.toArgb()
                window.navigationBarColor = MidnightNavy.toArgb()
                WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = false
                WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = false
            }
        }
    }

    MaterialTheme(
        colorScheme = CyberpunkDarkColorScheme,
        typography = Typography,
        content = content
    )
}

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false,
    content: @Composable () -> Unit
) {
    MediaVaultTheme(content = content)
}
