package com.example.ui.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

private val AppTypography = Typography()

private val DarkColorScheme = darkColorScheme(
  primary = PrimaryBlue,
  onPrimary = Color.White,
  primaryContainer = PrimaryHeaderNavy,
  onPrimaryContainer = Color.White,
  secondary = SuccessGreen,
  onSecondary = Color.White,
  secondaryContainer = SuccessGreenDark,
  onSecondaryContainer = Color.White,
  tertiary = ReviewPurple,
  onTertiary = Color.White,
  error = ErrorRed,
  onError = Color.White,
  background = DarkBackground,
  onBackground = TextPrimary,
  surface = DarkSurface,
  onSurface = TextPrimary,
  surfaceVariant = DarkSurfaceVariant,
  onSurfaceVariant = TextSecondary,
  outline = DarkBorder
)

@Composable
fun MyApplicationTheme(
  darkTheme: Boolean = true, // Default to sleek dark exam theme matching screenshots
  dynamicColor: Boolean = false,
  content: @Composable () -> Unit,
) {
  MaterialTheme(
    colorScheme = DarkColorScheme,
    typography = AppTypography,
    content = content
  )
}

