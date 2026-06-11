package com.example.ui.theme

import android.os.Build
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.dynamicDarkColorScheme
import androidx.compose.material3.dynamicLightColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext

private val DarkColorScheme =
  darkColorScheme(
    primary = DarkPrimaryEmerald,
    secondary = DarkSecondaryTerracotta,
    tertiary = AccentGold,
    background = DarkBackground,
    surface = DarkSurface,
    onPrimary = DarkBackground,
    onSecondary = DarkBackground,
    onTertiary = DarkBackground,
    onBackground = DarkTextLight,
    onSurface = DarkTextLight,
  )

private val LightColorScheme =
  lightColorScheme(
    primary = PrimaryEmerald,
    secondary = SecondaryTerracotta,
    tertiary = AccentGold,
    background = WarmSandBackground,
    surface = SurfaceIvory,
    onPrimary = OnPrimaryWhite,
    onSecondary = OnPrimaryWhite,
    onTertiary = OnPrimaryWhite,
    onBackground = TextDark,
    onSurface = TextDark,
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // For cultural branding we use our custom colors by default
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  val colorScheme =
    when {
      dynamicColor && Build.VERSION.SDK_INT >= Build.VERSION_CODES.S -> {
        val context = LocalContext.current
        if (darkTheme) dynamicDarkColorScheme(context) else dynamicLightColorScheme(context)
      }

      darkTheme -> DarkColorScheme
      else -> LightColorScheme
    }

  MaterialTheme(colorScheme = colorScheme, typography = Typography, content = content)
}
