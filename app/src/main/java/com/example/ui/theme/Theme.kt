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

import androidx.compose.ui.graphics.Color

private val DarkColorScheme =
  darkColorScheme(
    primary = YellowPrimary,
    secondary = YellowAccent,
    tertiary = AccentGreen,
    background = BrandBlack,
    surface = BrandDarkGrey,
    onPrimary = BrandBlack,
    onSecondary = Color.White,
    onBackground = Color.White,
    onSurface = Color.White
  )

private val LightColorScheme =
  lightColorScheme(
    primary = BrandBlack,
    primaryContainer = YellowPrimary,
    secondary = YellowAccent,
    tertiary = AccentGreen,
    background = LightBackground,   // Aligned with #F5F5F5 specification
    surface = Color.White,          // Standard white card surface
    onPrimary = Color.White,
    onSecondary = BrandBlack,
    onBackground = BrandBlack,
    onSurface = BrandBlack
  )

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = isSystemInDarkTheme(),
  // Branded transport style overrides dynamic light scheme
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
