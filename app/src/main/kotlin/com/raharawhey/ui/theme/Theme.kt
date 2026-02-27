package com.raharawhey.ui.theme

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

// ─── Islamic Luxury Palette ──────────────────────────────────────────────────
val IslamicGreen        = Color(0xFF1B5E20)
val IslamicGreenLight   = Color(0xFF2E7D32)
val IslamicGreenDark    = Color(0xFF003300)
val GoldAccent          = Color(0xFFC9A84C)
val GoldLight           = Color(0xFFE8C97A)
val GoldDark            = Color(0xFF8B6914)
val DeepNavy            = Color(0xFF0A0F1E)
val SurfaceDeep         = Color(0xFF111B2E)
val SurfaceCard         = Color(0xFF162035)
val SurfaceElevated     = Color(0xFF1A2640)
val TextPrimary         = Color(0xFFF0E8D0)
val TextSecondary       = Color(0xFFB0BEC5)
val TextMuted           = Color(0xFF607D8B)
val ErrorColor          = Color(0xFFCF6679)
val NextPrayerHighlight = Color(0xFF26A69A)

private val DarkColorScheme = darkColorScheme(
    primary          = GoldAccent,
    onPrimary        = Color(0xFF1A0F00),
    primaryContainer = GoldDark,
    onPrimaryContainer = GoldLight,
    secondary        = IslamicGreen,
    onSecondary      = Color.White,
    secondaryContainer = IslamicGreenLight,
    onSecondaryContainer = Color(0xFFE8F5E9),
    tertiary         = NextPrayerHighlight,
    onTertiary       = Color(0xFF00201E),
    background       = DeepNavy,
    onBackground     = TextPrimary,
    surface          = SurfaceDeep,
    onSurface        = TextPrimary,
    surfaceVariant   = SurfaceCard,
    onSurfaceVariant = TextSecondary,
    outline          = Color(0xFF37474F),
    outlineVariant   = Color(0xFF263238),
    error            = ErrorColor,
    onError          = Color.White,
)

@Composable
fun RahaRawheyTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = DarkColorScheme,
        typography  = RahaTypography,
        shapes      = RahaShapes,
        content     = content
    )
}
