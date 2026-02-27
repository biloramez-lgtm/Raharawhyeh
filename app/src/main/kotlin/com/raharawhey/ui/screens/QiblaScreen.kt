package com.raharawhey.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.*
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import com.raharawhey.ui.components.GoldDivider
import com.raharawhey.ui.theme.*
import kotlin.math.*

@Composable
fun QiblaScreen(
    viewModel: QiblaViewModel = hiltViewModel()
) {
    val state       by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    // Register/unregister sensors with lifecycle
    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_RESUME -> viewModel.startCompass()
                Lifecycle.Event.ON_PAUSE  -> viewModel.stopCompass()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose { lifecycleOwner.lifecycle.removeObserver(observer) }
    }

    // Animate compass rotation
    val animatedCompass by animateFloatAsState(
        targetValue  = -state.compassDegree,
        animationSpec = tween(200, easing = LinearEasing),
        label = "compass"
    )
    val animatedQiblaOffset = state.qiblaDirection - state.compassDegree

    Column(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(16.dp))

        // Title
        Text(
            text = "اتجاه القبلة",
            style = MaterialTheme.typography.headlineMedium,
            color = GoldAccent,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "الكعبة المشرفة · مكة المكرمة",
            style = MaterialTheme.typography.bodyMedium,
            color = TextSecondary
        )

        Spacer(Modifier.height(8.dp))
        GoldDivider()
        Spacer(Modifier.height(24.dp))

        if (!state.hasCompass) {
            CompassUnavailable(qiblaDirection = state.qiblaDirection.toInt())
        } else if (state.isLocating) {
            CircularProgressIndicator(color = GoldAccent)
            Spacer(Modifier.height(8.dp))
            Text("جاري تحديد الموقع...", color = TextSecondary)
        } else {
            // ─── Compass Canvas ───────────────────────────────────────
            CompassCanvas(
                compassDeg    = animatedCompass,
                qiblaOffsetDeg = animatedQiblaOffset,
                modifier = Modifier.size(300.dp)
            )

            Spacer(Modifier.height(24.dp))

            // ─── Qibla Angle Info ─────────────────────────────────────
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.5f)),
                shape  = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(20.dp),
                    horizontalArrangement = Arrangement.SpaceEvenly
                ) {
                    InfoItem(
                        label = "اتجاه القبلة",
                        value = "${state.qiblaDirection.toInt()}°"
                    )
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    InfoItem(
                        label = "المسافة للمكة",
                        value = "${state.distanceKm.toInt().formatWithCommas()} كم"
                    )
                    VerticalDivider(modifier = Modifier.height(50.dp))
                    InfoItem(
                        label = "الاتجاه الحالي",
                        value = "${state.compassDegree.toInt()}°"
                    )
                }
            }

            Spacer(Modifier.height(16.dp))

            // Accuracy indicator
            val (accText, accColor) = when (state.accuracy) {
                3 -> "دقة عالية ✓" to GoldAccent
                2 -> "دقة متوسطة" to NextPrayerHighlight
                else -> "دقة منخفضة - حرك الهاتف" to ErrorColor
            }
            Text(accText, color = accColor, style = MaterialTheme.typography.labelMedium)

            if (state.accuracy < 2) {
                Spacer(Modifier.height(8.dp))
                Text(
                    "لتحسين الدقة: حرك هاتفك في شكل ثماني (8) في الهواء",
                    color = TextMuted,
                    style = MaterialTheme.typography.bodySmall,
                    textAlign = TextAlign.Center
                )
            }
        }
    }
}

@Composable
private fun CompassCanvas(
    compassDeg: Float,
    qiblaOffsetDeg: Float,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        val cx = size.width / 2f
        val cy = size.height / 2f
        val r  = size.minDimension / 2f - 8.dp.toPx()

        drawContext.canvas.withSave {
            // ── Outer ring ──
            drawCircle(
                brush = Brush.radialGradient(
                    listOf(Color.Transparent, GoldDark.copy(alpha = 0.3f)),
                    center = Offset(cx, cy)
                ),
                radius = r + 8.dp.toPx()
            )
            drawCircle(color = GoldAccent.copy(alpha = 0.5f), radius = r + 4.dp.toPx(), style = Stroke(2f))
            drawCircle(color = SurfaceDeep,  radius = r)
            drawCircle(color = SurfaceCard,  radius = r, style = Stroke(1f))

            // ── Rotate entire compass ──
            rotate(compassDeg, pivot = Offset(cx, cy)) {
                // Cardinal directions
                val labels = listOf("N" to 0f, "E" to 90f, "S" to 180f, "W" to 270f)
                labels.forEach { (label, angle) ->
                    val rad = Math.toRadians(angle.toDouble())
                    val tx = cx + (r - 28.dp.toPx()) * sin(rad).toFloat()
                    val ty = cy - (r - 28.dp.toPx()) * cos(rad).toFloat()

                    val cardinalColor = if (label == "N") Color.Red else GoldAccent
                    drawContext.canvas.nativeCanvas.apply {
                        val paint = android.graphics.Paint().apply {
                            color = cardinalColor.toArgb()
                            textSize = 18.dp.toPx()
                            textAlign = android.graphics.Paint.Align.CENTER
                            isFakeBoldText = true
                        }
                        drawText(label, tx, ty + 7.dp.toPx(), paint)
                    }

                    // Tick mark
                    val innerR = r - 12.dp.toPx()
                    val outerR = r - 4.dp.toPx()
                    drawLine(
                        color = cardinalColor,
                        start = Offset(cx + innerR * sin(rad).toFloat(), cy - innerR * cos(rad).toFloat()),
                        end   = Offset(cx + outerR * sin(rad).toFloat(), cy - outerR * cos(rad).toFloat()),
                        strokeWidth = 2.5f
                    )
                }

                // Small ticks every 10 degrees
                for (deg in 0 until 360 step 10) {
                    if (deg % 90 == 0) continue
                    val rad = Math.toRadians(deg.toDouble())
                    val len = if (deg % 45 == 0) 10.dp.toPx() else 6.dp.toPx()
                    val innerR = r - 4.dp.toPx() - len
                    drawLine(
                        color = GoldAccent.copy(alpha = 0.3f),
                        start = Offset(cx + innerR * sin(rad).toFloat(), cy - innerR * cos(rad).toFloat()),
                        end   = Offset(cx + (r - 4.dp.toPx()) * sin(rad).toFloat(), cy - (r - 4.dp.toPx()) * cos(rad).toFloat()),
                        strokeWidth = 1f
                    )
                }
            }

            // ── Qibla Arrow (rotates with qibla offset) ──────────────────
            rotate(qiblaOffsetDeg, pivot = Offset(cx, cy)) {
                val arrowLen = r * 0.65f

                // Kaaba symbol at tip
                drawCircle(
                    color = IslamicGreenLight,
                    radius = 12.dp.toPx(),
                    center = Offset(cx, cy - arrowLen)
                )
                drawContext.canvas.nativeCanvas.apply {
                    val p = android.graphics.Paint().apply {
                        textSize = 14.dp.toPx()
                        textAlign = android.graphics.Paint.Align.CENTER
                    }
                    drawText("🕋", cx, cy - arrowLen + 5.dp.toPx(), p)
                }

                // Arrow shaft
                val path = Path().apply {
                    moveTo(cx, cy - arrowLen + 12.dp.toPx())
                    lineTo(cx - 6.dp.toPx(), cy + arrowLen * 0.1f)
                    lineTo(cx + 6.dp.toPx(), cy + arrowLen * 0.1f)
                    close()
                }
                drawPath(
                    path = path,
                    brush = Brush.linearGradient(
                        colors = listOf(IslamicGreenLight, GoldAccent),
                        start  = Offset(cx, cy - arrowLen),
                        end    = Offset(cx, cy)
                    )
                )
            }

            // ── Center dot ──
            drawCircle(color = GoldAccent, radius = 8.dp.toPx())
            drawCircle(color = DeepNavy,   radius = 4.dp.toPx())
        }
    }
}

@Composable
private fun InfoItem(label: String, value: String) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(value, style = MaterialTheme.typography.titleMedium, color = GoldAccent, fontWeight = FontWeight.Bold)
        Text(label, style = MaterialTheme.typography.labelSmall, color = TextSecondary)
    }
}

@Composable
private fun CompassUnavailable(qiblaDirection: Int) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Icon(Icons.Default.Warning, null, tint = ErrorColor, modifier = Modifier.size(48.dp))
        Spacer(Modifier.height(8.dp))
        Text("البوصلة غير متوفرة في هذا الجهاز", color = TextPrimary, textAlign = TextAlign.Center)
        Spacer(Modifier.height(8.dp))
        Text("يمكنك استخدام اتجاه القبلة: $qiblaDirection°", color = TextSecondary, textAlign = TextAlign.Center)
    }
}

private fun Int.formatWithCommas(): String {
    return String.format("%,d", this)
}
