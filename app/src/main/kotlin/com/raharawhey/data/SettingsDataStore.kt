package com.raharawhey.data

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsDataStore @Inject constructor(
    @ApplicationContext private val context: Context
) {

    private val calcMethodKey = intPreferencesKey("calculation_method")
    private val timeFormatKey = booleanPreferencesKey("time_format_12h")
    private val vibrationKey  = booleanPreferencesKey("vibration_enabled")

    val calculationMethod = context.dataStore.data.map { it[calcMethodKey] ?: 4 }
    val timeFormat12h = context.dataStore.data.map { it[timeFormatKey] ?: false }
    val vibrationEnabled = context.dataStore.data.map { it[vibrationKey] ?: true }

    suspend fun setCalculationMethod(method: Int) {
        context.dataStore.edit { it[calcMethodKey] = method }
    }

    suspend fun setTimeFormat12h(enabled: Boolean) {
        context.dataStore.edit { it[timeFormatKey] = enabled }
    }

    suspend fun setVibration(enabled: Boolean) {
        context.dataStore.edit { it[vibrationKey] = enabled }
    }
}
