package com.raharawhey.ui.screens

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.raharawhey.data.SettingsDataStore
import com.raharawhey.data.models.PrayerEntry
import com.raharawhey.data.models.PrayerTimesCache
import com.raharawhey.data.repository.PrayerRepository
import com.raharawhey.data.repository.Result
import com.raharawhey.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.time.Duration
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject

data class PrayerUiState(
    val isLoading: Boolean = false,
    val error: String? = null,
    val prayers: List<PrayerEntry> = emptyList(),
    val cache: PrayerTimesCache? = null,
    val currentTime: String = "",
    val currentDate: String = "",
    val hijriDate: String = "",
    val cityName: String = "جاري تحديد الموقع...",
    val timezone: String? = null,
    val latitude: Double = 21.3891,
    val longitude: Double = 39.8579,
    val calculationMethod: Int = 4,
    val is12HourFormat: Boolean = false,
    val vibrationEnabled: Boolean = true,
    val nextPrayerCountdown: String = "",
    val nextPrayerName: String = ""
)

@OptIn(ExperimentalCoroutinesApi::class)
@HiltViewModel
class PrayerTimesViewModel @Inject constructor(
    application: Application,
    private val repository: PrayerRepository,
    private val settings: SettingsDataStore
) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(PrayerUiState())
    val uiState: StateFlow<PrayerUiState> = _uiState.asStateFlow()

    private val dateFmt = SimpleDateFormat("EEEE، d MMMM yyyy", Locale("ar"))

    private val locationFlow = MutableStateFlow<Pair<Double, Double>?>(null)

    init {
        startClock()
        observeSettings()
        observePrayerData()
        loadLocation() // Initial load
    }

    private fun observeSettings() {
        settings.timeFormat12h.onEach { is12h ->
            _uiState.update { it.copy(is12HourFormat = is12h) }
        }.launchIn(viewModelScope)

        settings.vibrationEnabled.onEach { isEnabled ->
            _uiState.update { it.copy(vibrationEnabled = isEnabled) }
        }.launchIn(viewModelScope)
    }

    private fun observePrayerData() {
        locationFlow.filterNotNull()
            .combine(settings.calculationMethod) { location, method -> location to method }
            .distinctUntilChanged()
            .flatMapLatest { (location, method) ->
                _uiState.update { it.copy(calculationMethod = method) }
                repository.getPrayerTimes(location.first, location.second, method)
            }
            .onEach { result ->
                when (result) {
                    is Result.Loading -> _uiState.update { it.copy(isLoading = true, error = null) }
                    is Result.Success -> {
                        val prayers = repository.buildPrayerEntries(result.data)
                        _uiState.update { state ->
                            state.copy(
                                isLoading = false,
                                prayers = prayers,
                                cache = result.data,
                                timezone = result.data.timezone,
                                hijriDate = "${result.data.hijriDate} ${result.data.hijriMonth} ${result.data.hijriYear} هـ"
                            )
                        }
                    }
                    is Result.Error -> _uiState.update { it.copy(isLoading = false, error = result.message) }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun startClock() {
        viewModelScope.launch {
            while (true) {
                // THIS IS THE FIX: Always use the device's default timezone for the main clock.
                val deviceZoneId = ZoneId.systemDefault()
                val now = ZonedDateTime.now(deviceZoneId)
                
                val timeFmt = if (_uiState.value.is12HourFormat) {
                    DateTimeFormatter.ofPattern("hh:mm:ss a", Locale.forLanguageTag("ar"))
                } else {
                    DateTimeFormatter.ofPattern("HH:mm:ss")
                }

                _uiState.update { it.copy(
                    currentTime = now.format(timeFmt),
                    currentDate = dateFmt.format(Date.from(now.toInstant()))
                )}
                
                updateCountdown()
                delay(1000)
            }
        }
    }

    private fun loadLocation() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            locationFlow.value = LocationUtils.getCurrentLocation(ctx) ?: (21.3891 to 39.8579)
        }
    }

    fun refresh() {
        loadLocation()
    }

    fun setCalculationMethod(method: Int) {
        viewModelScope.launch {
            settings.setCalculationMethod(method)
        }
    }

    fun setTimeFormat(is12h: Boolean) {
        viewModelScope.launch {
            settings.setTimeFormat12h(is12h)
        }
    }

    fun setVibration(enabled: Boolean) {
        viewModelScope.launch {
            settings.setVibration(enabled)
        }
    }

    private fun updateCountdown() {
        val state = _uiState.value
        val nextPrayer = state.prayers.firstOrNull { it.isNext } ?: return
        val timezone = state.timezone ?: return

        try {
            val prayerZoneId = ZoneId.of(timezone)
            val nowInPrayerZone = ZonedDateTime.now(prayerZoneId)
            val prayerTime = LocalTime.parse(nextPrayer.time, DateTimeFormatter.ofPattern("HH:mm"))
            var prayerDateTime = nowInPrayerZone.with(prayerTime)

            if (nowInPrayerZone.toLocalTime().isAfter(prayerTime)) {
                prayerDateTime = prayerDateTime.plusDays(1)
            }

            val duration = Duration.between(nowInPrayerZone, prayerDateTime)
            if (duration.isNegative || duration.isZero) {
                 _uiState.update { it.copy(nextPrayerCountdown = "الآن") }
                return
            }
            val hours = duration.toHours()
            val minutes = duration.toMinutes() % 60

            val label = if (hours > 0) "${hours}س ${minutes}د" else "${minutes} دقيقة"
            _uiState.update { it.copy(nextPrayerCountdown = label, nextPrayerName = nextPrayer.nameAr) }

        } catch (e: Exception) {
            _uiState.update { it.copy(nextPrayerCountdown = "", nextPrayerName = "") }
        }
    }
}
