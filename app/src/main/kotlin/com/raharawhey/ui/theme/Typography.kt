package com.raharawhey.ui.theme

import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.RoundedCornerShape

// Use system Arabic fonts; replace with bundled font files if needed
val AmiriFamily = FontFamily.Default  // swap with FontFamily(Font(R.font.amiri_regular)) after adding font
val CairoFamily = FontFamily.Default  // swap with FontFamily(Font(R.font.cairo_regular))

val RahaTypography = Typography(
    displayLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 57.sp,
        lineHeight = 64.sp,
        letterSpacing = (-0.25).sp
    ),
    displayMedium = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 45.sp,
        lineHeight = 52.sp
    ),
    headlineLarge = TextStyle(
        fontFamily = AmiriFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 32.sp,
        lineHeight = 40.sp
    ),
    headlineMedium = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 28.sp,
        lineHeight = 36.sp
    ),
    titleLarge = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.Bold,
        fontSize   = 22.sp,
        lineHeight = 28.sp
    ),
    titleMedium = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.SemiBold,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyLarge = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 16.sp,
        lineHeight = 24.sp
    ),
    bodyMedium = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.Normal,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    labelLarge = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 14.sp,
        lineHeight = 20.sp
    ),
    labelMedium = TextStyle(
        fontFamily = CairoFamily,
        fontWeight = FontWeight.Medium,
        fontSize   = 12.sp,
        lineHeight = 16.sp
    ),
)

val RahaShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small      = RoundedCornerShape(8.dp),
    medium     = RoundedCornerShape(16.dp),
    large      = RoundedCornerShape(24.dp),
    extraLarge = RoundedCornerShape(32.dp)
)
