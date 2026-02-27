package com.raharawhey.ui.components

import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.raharawhey.R
import com.raharawhey.ui.theme.*
import java.time.LocalTime
import java.time.format.DateTimeFormatter
import java.util.Locale
import kotlin.math.*

// ─── Islamic Star Background Pattern ─────────────────────────────────────────
@Composable
fun IslamicBackground(modifier: Modifier = Modifier, content: @Composable BoxScope.() -> Unit) {
    Box(
        modifier = modifier
    ) {
        Image(
            painter = painterResource(id = R.drawable.mosque_background),
            contentDescription = null,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize(),
            colorFilter = ColorFilter.tint(GoldAccent.copy(alpha = 0.3f))
        )
        content()
    }
}

private fun DrawScope.drawIslamicPattern() {
    val paint = Paint().apply {
        color = GoldAccent.copy(alpha = 0.04f)
        style = PaintingStyle.Stroke
        strokeWidth = 1f
    }
    val step = 120f
    var x = 0f
    while (x < size.width + step) {
        var y = 0f
        while (y < size.height + step) {
            drawIslamicStar(Offset(x, y), 28f, paint)
            y += step
        }
        x += step
    }
}

private fun DrawScope.drawIslamicStar(center: Offset, radius: Float, paint: Paint) {
    val path = Path()
    val points = 8
    for (i in 0 until points * 2) {
        val angle = (i * PI / points - PI / 2).toFloat()
        val r = if (i % 2 == 0) radius else radius * 0.4f
        val x = center.x + r * cos(angle)
        val y = center.y + r * sin(angle)
        if (i == 0) path.moveTo(x, y) else path.lineTo(x, y)
    }
    path.close()
    drawPath(path, paint.color, style = Stroke(1f))
}

// ─── Gold Divider ─────────────────────────────────────────────────────────────
@Composable
fun GoldDivider(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(1.dp)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(Color.Transparent, GoldAccent, Color.Transparent)
                )
            )
    )
}

// ─── Next Prayer Countdown Card ───────────────────────────────────────────────
@Composable
fun NextPrayerBanner(
    prayerName: String,
    countdown: String,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue  = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(1500, easing = EaseInOut),
            repeatMode = RepeatMode.Reverse
        ),
        label = "alpha"
    )

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFF1A2A20)
        ),
        shape = MaterialTheme.shapes.large
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 20.dp, vertical = 14.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = "الصلاة القادمة",
                    style = MaterialTheme.typography.labelMedium,
                    color = TextSecondary
                )
                Text(
                    text = prayerName,
                    style = MaterialTheme.typography.titleMedium,
                    color = GoldAccent,
                    fontWeight = FontWeight.Bold
                )
            }
            Box(
                modifier = Modifier
                    .clip(MaterialTheme.shapes.medium)
                    .background(NextPrayerHighlight.copy(alpha = 0.15f))
                    .padding(horizontal = 16.dp, vertical = 8.dp)
            ) {
                Text(
                    text = countdown,
                    style = MaterialTheme.typography.titleMedium,
                    color = NextPrayerHighlight.copy(alpha = alpha),
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// ─── Prayer Row Card ──────────────────────────────────────────────────────────
@Composable
fun PrayerRowCard(
    prayer: com.raharawhey.data.models.PrayerEntry,
    is12HourFormat: Boolean,
    timezone: String, // This is here for context but should not be used for formatting LocalTime
    modifier: Modifier = Modifier
) {
    val cardColor = when {
        prayer.isNext   -> Color(0xFF1A2A20)
        prayer.isPassed -> SurfaceCard.copy(alpha = 0.5f)
        else     -> SurfaceCard
    }
    val textColor = when {
        prayer.isNext   -> TextPrimary
        prayer.isPassed -> TextMuted
        else     -> TextSecondary
    }
    val timeColor = when {
        prayer.isNext   -> GoldAccent
        prayer.isPassed -> TextMuted
        else     -> TextPrimary
    }

    val formattedTime = remember(prayer.time, is12HourFormat) { 
        try {
            val prayerLocalTime = LocalTime.parse(prayer.time, DateTimeFormatter.ofPattern("HH:mm"))
            
            if (is12HourFormat) {
                val displayFormatter = DateTimeFormatter.ofPattern("hh:mm a", Locale.forLanguageTag("ar"))
                prayerLocalTime.format(displayFormatter)
            } else {
                prayer.time
            }
        } catch (e: Exception) {
            prayer.time
        }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors   = CardDefaults.cardColors(containerColor = cardColor),
        shape    = MaterialTheme.shapes.medium
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Time (left side in RTL)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = formattedTime, style = MaterialTheme.typography.titleMedium, color = timeColor, fontWeight = FontWeight.Bold)
                if (prayer.isNext) {
                    Box(
                        Modifier
                            .size(8.dp)
                            .clip(CircleShape)
                            .background(GoldAccent)
                    )
                }
            }
            // Name + icon (right side in RTL)
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(text = prayer.nameAr, style = MaterialTheme.typography.bodyLarge, color = textColor, fontWeight = if (prayer.isNext) FontWeight.Bold else FontWeight.Normal)
                Text(text = prayer.icon, fontSize = 22.sp)
            }
        }
    }
}

// ─── Section Header ───────────────────────────────────────────────────────────
@Composable
fun SectionHeader(title: String, modifier: Modifier = Modifier) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = GoldAccent,
        modifier = modifier.padding(vertical = 8.dp)
    )
}
