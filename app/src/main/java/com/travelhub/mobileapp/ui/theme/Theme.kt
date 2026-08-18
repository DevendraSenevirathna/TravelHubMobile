package com.travelhub.mobileapp.ui.theme

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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

private val LightColorScheme = lightColorScheme(
    primary = ForestGreen,
    onPrimary = SoftWhite,
    primaryContainer = ForestGreenLight,
    onPrimaryContainer = TextDarkGray,

    secondary = SkyBlue,
    onSecondary = TextDarkGray,
    secondaryContainer = SkyBlueLight,
    onSecondaryContainer = TextDarkGray,

    tertiary = SandBeige,
    onTertiary = TextDarkGray,

    background = SoftWhite,
    onBackground = TextDarkGray,

    surface = SurfaceLight,
    onSurface = TextDarkGray,

    error = ErrorRed,
    onError = SoftWhite,

    outline = OutlineGray
)

private val DarkColorScheme = darkColorScheme(
    primary = ForestGreenLight,
    onPrimary = TextDarkGray,
    primaryContainer = ForestGreenDark,
    onPrimaryContainer = SoftWhite,

    secondary = SkyBlueLight,
    onSecondary = TextDarkGray,

    tertiary = SandBeigeDark,
    onTertiary = SoftWhite,

    background = Color(0xFF1B1B1B),
    onBackground = SoftWhite,

    surface = Color(0xFF242424),
    onSurface = SoftWhite,

    error = ErrorRed,
    onError = SoftWhite
)

@Composable
fun TravelHubTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    // Keep dynamic color OFF by default — TravelHub has a defined brand
    // palette (forest green / sky blue), and Material You dynamic
    // theming would override it with the user's wallpaper colors.
    dynamicColor: Boolean = false,
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
            window.statusBarColor = colorScheme.primary.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = TravelHubTypography,
        content = content
    )
}