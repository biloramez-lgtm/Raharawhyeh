package com.raharawhey.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.Vibration
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raharawhey.data.models.CalculationMethodEnum
import com.raharawhey.ui.components.GoldDivider
import com.raharawhey.ui.theme.GoldAccent
import com.raharawhey.ui.theme.GoldDark
import com.raharawhey.ui.theme.SurfaceCard
import com.raharawhey.ui.theme.TextMuted
import com.raharawhey.ui.theme.TextPrimary
import com.raharawhey.ui.theme.TextSecondary

@Composable
fun SettingsScreen(
    viewModel: PrayerTimesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var notificationsEnabled by remember { mutableStateOf(true) }
    var adhanEnabled         by remember { mutableStateOf(true) }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Text(
                "الإعدادات",
                style = MaterialTheme.typography.headlineMedium,
                color = GoldAccent,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(vertical = 8.dp)
            )
            GoldDivider()
            Spacer(Modifier.height(8.dp))
        }

        // General group
        item { SettingGroupHeader("عام") }
        item {
            SettingToggleRow(
                icon = Icons.Default.Schedule,
                title = "نمط الساعة (12/24)",
                checked = state.is12HourFormat,
                onCheckedChange = { viewModel.setTimeFormat(it) }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }

        // Notifications group
        item { SettingGroupHeader("الإشعارات") }

        item {
            SettingToggleRow(
                icon    = Icons.Default.Notifications,
                title   = "إشعارات الصلاة",
                checked = notificationsEnabled,
                onCheckedChange = { notificationsEnabled = it }
            )
        }
        item {
            SettingToggleRow(
                icon    = Icons.Default.VolumeUp,
                title   = "الأذان",
                checked = adhanEnabled,
                onCheckedChange = { adhanEnabled = it }
            )
        }
        item {
            SettingToggleRow(
                icon    = Icons.Default.Vibration,
                title   = "الاهتزاز",
                checked = state.vibrationEnabled,
                onCheckedChange = { viewModel.setVibration(it) }
            )
        }

        item { Spacer(Modifier.height(8.dp)) }
        item { SettingGroupHeader("حول التطبيق") }

        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.5f)),
                shape  = MaterialTheme.shapes.large,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier
                        .padding(20.dp)
                        .fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text("🕌", style = MaterialTheme.typography.displaySmall)
                    Spacer(Modifier.height(8.dp))
                    Text("رها روحي", style = MaterialTheme.typography.titleLarge, color = GoldAccent, fontWeight = FontWeight.Bold)
                    Text("Raha Rawhey", style = MaterialTheme.typography.bodyMedium, color = TextSecondary)
                    Spacer(Modifier.height(4.dp))
                    Text("الإصدار 1.0.0", style = MaterialTheme.typography.labelMedium, color = TextMuted)
                    Spacer(Modifier.height(12.dp))
                    GoldDivider()
                    Spacer(Modifier.height(12.dp))
                    Text(
                        "بيانات أوقات الصلاة مقدمة من\naladhan.com API",
                        style = MaterialTheme.typography.bodySmall,
                        color = TextMuted,
                        textAlign = TextAlign.Center
                    )
                }
            }
        }
    }
}

@Composable
private fun SettingGroupHeader(title: String) {
    Text(
        title,
        style = MaterialTheme.typography.labelLarge,
        color = GoldAccent,
        modifier = Modifier.padding(bottom = 4.dp)
    )
}

@Composable
private fun SettingToggleRow(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Card(
        colors = CardDefaults.cardColors(containerColor = SurfaceCard.copy(alpha = 0.5f)),
        shape  = MaterialTheme.shapes.medium,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Switch(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = GoldAccent,
                    checkedTrackColor = GoldDark
                )
            )
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Text(title, style = MaterialTheme.typography.bodyLarge, color = TextPrimary)
                Icon(icon, null, tint = GoldAccent.copy(alpha = 0.7f), modifier = Modifier.size(22.dp))
            }
        }
    }
}
