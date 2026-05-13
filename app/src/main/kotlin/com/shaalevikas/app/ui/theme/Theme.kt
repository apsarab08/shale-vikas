package com.shaalevikas.app.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

private val ShaaleColorScheme = lightColorScheme(
    primary          = GreenPrimary,
    onPrimary        = White,
    primaryContainer = GreenLight,
    onPrimaryContainer = GreenMid,
    secondary        = Teal,
    onSecondary      = White,
    secondaryContainer = TealLight,
    background       = Surface,
    onBackground     = TextPrimary,
    surface          = White,
    onSurface        = TextPrimary,
    surfaceVariant   = Surface,
    onSurfaceVariant = TextSecondary,
    error            = ErrorRed,
    onError          = White,
    errorContainer   = ErrorLight,
    outline          = Divider,
)

val ShaaleTypography = Typography(
    headlineLarge = TextStyle(fontSize = 28.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    headlineMedium = TextStyle(fontSize = 22.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    headlineSmall = TextStyle(fontSize = 18.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleLarge = TextStyle(fontSize = 20.sp, fontWeight = FontWeight.Bold, color = TextPrimary),
    titleMedium = TextStyle(fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = TextPrimary),
    titleSmall = TextStyle(fontSize = 14.sp, fontWeight = FontWeight.Medium, color = TextPrimary),
    bodyLarge = TextStyle(fontSize = 16.sp, color = TextPrimary),
    bodyMedium = TextStyle(fontSize = 14.sp, color = TextPrimary),
    bodySmall = TextStyle(fontSize = 12.sp, color = TextSecondary),
    labelSmall = TextStyle(fontSize = 11.sp, color = TextSecondary),
)

@Composable
fun ShaaleVikasTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = ShaaleColorScheme,
        typography = ShaaleTypography,
        content = content
    )
}
