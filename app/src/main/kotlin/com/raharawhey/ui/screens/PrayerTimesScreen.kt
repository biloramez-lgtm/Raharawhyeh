package com.raharawhey.ui.screens

import androidx.compose.animation.*
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.raharawhey.data.models.CalculationMethodEnum
import com.raharawhey.ui.components.*
import com.raharawhey.ui.theme.*

@Composable
fun PrayerTimesScreen(
    viewModel: PrayerTimesViewModel = hiltViewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // ── Header: Clock & Date ──────────────────────────────────────────
        item {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Arabic Time
                Text(
                    text = state.currentTime,
                    style = MaterialTheme.typography.displayMedium.copy(
                        fontWeight = FontWeight.Bold,
                        letterSpacing = 2.sp
                    ),
                    color = GoldAccent
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.currentDate,
                    style = MaterialTheme.typography.bodyLarge,
                    color = TextSecondary,
                    textAlign = TextAlign.Center
                )
                if (state.hijriDate.isNotEmpty()) {
                    Spacer(Modifier.height(2.dp))
                    Text(
                        text = state.hijriDate,
                        style = MaterialTheme.typography.bodyMedium,
                        color = GoldAccent.copy(alpha = 0.7f),
                        textAlign = TextAlign.Center
                    )
                }
                Spacer(Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(Icons.Default.LocationOn, null, tint = TextMuted, modifier = Modifier.size(14.dp))
                    Text(text = state.cityName, style = MaterialTheme.typography.labelMedium, color = TextMuted)
                }
            }
        }

        // ── Gold Divider ─────────────────────────────────────────────────
        item { GoldDivider() }

        // ── Next Prayer Banner ───────────────────────────────────────────
        if (state.nextPrayerName.isNotEmpty()) {
            item {
                NextPrayerBanner(
                    prayerName = state.nextPrayerName,
                    countdown  = state.nextPrayerCountdown
                )
            }
        }

        // ── Loading / Error ──────────────────────────────────────────────
        if (state.isLoading) {
            item {
                Box(Modifier.fillMaxWidth().height(120.dp), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator(color = GoldAccent)
                }
            }
        }

        state.error?.let { err ->
            item {
                Card(
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        IconButton(onClick = { viewModel.refresh() }) {
                            Icon(Icons.Default.Refresh, null, tint = GoldAccent)
                        }
                        Text(err, color = MaterialTheme.colorScheme.onErrorContainer, style = MaterialTheme.typography.bodyMedium, textAlign = TextAlign.End)
                    }
                }
            }
        }

        // ── Prayer Times List ────────────────────────────────────────────
        if (state.prayers.isNotEmpty()) {
            item { SectionHeader("أوقات الصلاة") }

            items(state.prayers) { prayer ->
                state.timezone?.let {
                    PrayerRowCard(
                        prayer = prayer,
                        is12HourFormat = state.is12HourFormat,
                        timezone = it
                    )
                }
            }
        }

        // ── Calculation Method Selector ──────────────────────────────────
        item {
            Spacer(Modifier.height(8.dp))
            SectionHeader("طريقة الحساب")
        }
        item {
            CalculationMethodSelector(
                selectedMethod = state.calculationMethod,
                onMethodSelected = { viewModel.setCalculationMethod(it) }
            )
        }
        item { Spacer(Modifier.height(16.dp)) }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CalculationMethodSelector(
    selectedMethod: Int,
    onMethodSelected: (Int) -> Unit
) {
    val methods = remember {
        CalculationMethodEnum.values().map { it.id to it.nameAr }
    }

    var expanded by remember { mutableStateOf(false) }
    val selected = methods.firstOrNull { it.first == selectedMethod }?.second ?: methods.first().second

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it }
    ) {
        OutlinedTextField(
            value = selected,
            onValueChange = {},
            readOnly = true,
            label = { Text("طريقة الحساب") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor   = GoldAccent,
                unfocusedBorderColor = TextMuted,
                focusedLabelColor    = GoldAccent,
                unfocusedLabelColor  = TextSecondary,
                focusedTextColor     = TextPrimary,
                unfocusedTextColor   = TextPrimary
            ),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            methods.forEach { (id, name) ->
                DropdownMenuItem(
                    text = { Text(name) },
                    onClick = {
                        onMethodSelected(id)
                        expanded = false
                    }
                )
            }
        }
    }
}
