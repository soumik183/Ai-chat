package com.app.ai.mclint.core.theme

import android.app.Activity
import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

/**
 * Light color scheme for the application
 */
private val LightColorScheme = lightColorScheme(
    primary = Primary,
    onPrimary = OnBackgroundDark,
    primaryContainer = PrimaryLight,
    onPrimaryContainer = PrimaryDark,
    
    secondary = Secondary,
    onSecondary = OnBackgroundDark,
    secondaryContainer = SecondaryLight,
    onSecondaryContainer = SecondaryDark,
    
    tertiary = Accent,
    onTertiary = OnBackgroundDark,
    
    error = Error,
    onError = OnBackgroundDark,
    errorContainer = Error.copy(alpha = 0.1f),
    onErrorContainer = Error,
    
    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = SurfaceVariantLight,
    onSurfaceVariant = OnSurfaceVariantLight,
    
    outline = OutlineLight,
    outlineVariant = OutlineLight.copy(alpha = 0.5f),
    
    inverseSurface = SurfaceDark,
    inverseOnSurface = OnSurfaceDark,
    inversePrimary = PrimaryLight
)

/**
 * Dark color scheme for the application
 */
private val DarkColorScheme = darkColorScheme(
    primary = PrimaryLight,
    onPrimary = OnBackgroundDark,
    primaryContainer = PrimaryDark,
    onPrimaryContainer = PrimaryLight,
    
    secondary = SecondaryLight,
    onSecondary = OnBackgroundDark,
    secondaryContainer = SecondaryDark,
    onSecondaryContainer = SecondaryLight,
    
    tertiary = Accent,
    onTertiary = OnBackgroundDark,
    
    error = Error,
    onError = OnBackgroundDark,
    errorContainer = Error.copy(alpha = 0.2f),
    onErrorContainer = Error,
    
    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = SurfaceVariantDark,
    onSurfaceVariant = OnSurfaceVariantDark,
    
    outline = OutlineDark,
    outlineVariant = OutlineDark.copy(alpha = 0.5f),
    
    inverseSurface = SurfaceLight,
    inverseOnSurface = OnSurfaceLight,
    inversePrimary = PrimaryDark
)

/**
 * Main application theme composable
 * 
 * @param darkTheme Whether to use dark theme. Defaults to system setting.
 * @param dynamicColor Whether to use dynamic colors (Android 12+). Defaults to true.
 * @param content The content to be themed
 */
@Composable
fun AiFileManagerTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    dynamicColor: Boolean = true,
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
    
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            window.navigationBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
            WindowCompat.getInsetsController(window, view).isAppearanceLightNavigationBars = !darkTheme
        }
    }
    
    MaterialTheme(
        colorScheme = colorScheme,
        typography = AppTypography,
        content = content
    )
}

/**
 * Theme mode enum for settings
 */
enum class ThemeMode {
    LIGHT,
    DARK,
    SYSTEM
}

/**
 * Convert ThemeMode to darkTheme boolean
 */
fun ThemeMode.toDarkTheme(): @Composable () -> Boolean = when (this) {
    ThemeMode.LIGHT -> { { false } }
    ThemeMode.DARK -> { { true } }
    ThemeMode.SYSTEM -> { { isSystemInDarkTheme() } }
}
