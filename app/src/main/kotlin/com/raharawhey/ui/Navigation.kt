package com.raharawhey.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.WbSunny
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val labelAr: String, val icon: ImageVector) {
    object PrayerTimes : Screen("prayer_times", "مواقيت الصلاة", Icons.Default.WbSunny)
    object Qibla       : Screen("qibla",        "القبلة",         Icons.Default.Explore)
    object Settings    : Screen("settings",     "الإعدادات",      Icons.Default.Settings)

    companion object {
        val all = listOf(PrayerTimes, Qibla, Settings)
    }
}
