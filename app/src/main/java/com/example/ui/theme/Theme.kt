package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

private val LightColorScheme = lightColorScheme(
    primary = BluePrimary,
    onPrimary = TextOnPrimary,
    primaryContainer = BlueContainer,
    onPrimaryContainer = BluePrimaryDark,
    secondary = CyanSecondary,
    onSecondary = Color.White,
    secondaryContainer = CyanContainer,
    onSecondaryContainer = Color(0xFF0369A1),
    tertiary = TertiaryIndigo,
    onTertiary = Color.White,
    background = SurfaceOffWhite,
    onBackground = TextPrimary,
    surface = SurfacePureWhite,
    onSurface = TextPrimary,
    surfaceVariant = SurfaceCardAlt,
    onSurfaceVariant = TextSecondary,
    outline = BlueContainerBorder,
    outlineVariant = Color(0xFFE2E8F0),
    error = ErrorRed,
    errorContainer = ErrorContainer,
    onError = Color.White,
    onErrorContainer = Color(0xFF991B1B)
)

private val DarkColorScheme = darkColorScheme(
    primary = BluePrimaryLight,
    onPrimary = Color(0xFF00297A),
    primaryContainer = Color(0xFF1E3A8A),
    onPrimaryContainer = Color(0xFFDBEAFE),
    secondary = Color(0xFF38BDF8),
    onSecondary = Color(0xFF003554),
    secondaryContainer = Color(0xFF0369A1),
    onSecondaryContainer = Color(0xFFE0F2FE),
    tertiary = Color(0xFF818CF8),
    background = Color(0xFF0B1120),
    onBackground = Color(0xFFF1F5F9),
    surface = Color(0xFF111827),
    onSurface = Color(0xFFF1F5F9),
    surfaceVariant = Color(0xFF1F2937),
    onSurfaceVariant = Color(0xFF94A3B8),
    outline = Color(0xFF334155),
    outlineVariant = Color(0xFF1E293B),
    error = Color(0xFFF87171),
    errorContainer = Color(0xFF7F1D1D),
    onError = Color(0xFF450A0A)
)

@Composable
fun MyApplicationTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = false, // Keep branded professional blue & white theme
    content: @Composable () -> Unit
) {
    val colorScheme = when {
        dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
            val context = LocalContext.current
            if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
        }
        darkTheme -> DarkColorScheme
        else -> LightColorScheme
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        content = content
    )
}
