package com.raharawhey.ui.screens

import android.app.Application
import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.raharawhey.utils.LocationUtils
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QiblaUiState(
    val compassDegree: Float = 0f,
    val qiblaDirection: Float = 0f,
    val latitude: Double = 21.3891,
    val longitude: Double = 39.8579,
    val distanceKm: Double = 0.0,
    val isLocating: Boolean = true,
    val hasCompass: Boolean = true,
    val accuracy: Int = SensorManager.SENSOR_STATUS_ACCURACY_MEDIUM
)

@HiltViewModel
class QiblaViewModel @Inject constructor(
    application: Application
) : AndroidViewModel(application), SensorEventListener {

    private val sensorManager = application.getSystemService(Context.SENSOR_SERVICE) as SensorManager
    private val accelerometerReading = FloatArray(3)
    private val magnetometerReading  = FloatArray(3)
    private val rotationMatrix       = FloatArray(9)
    private val orientationAngles    = FloatArray(3)

    private val _uiState = MutableStateFlow(QiblaUiState())
    val uiState: StateFlow<QiblaUiState> = _uiState.asStateFlow()

    init {
        val hasMag = sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD) != null
        _uiState.update { it.copy(hasCompass = hasMag) }
        loadLocation()
    }

    private fun loadLocation() {
        viewModelScope.launch {
            val ctx = getApplication<Application>()
            val loc = LocationUtils.getCurrentLocation(ctx)
            val lat = loc?.first ?: 21.3891
            val lng = loc?.second ?: 39.8579
            val qibla = LocationUtils.calculateQiblaDirection(lat, lng)
            val dist  = LocationUtils.distanceToMakkah(lat, lng)
            _uiState.update { it.copy(
                latitude = lat,
                longitude = lng,
                qiblaDirection = qibla,
                distanceKm = dist,
                isLocating = false
            )}
        }
    }

    fun startCompass() {
        sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
        sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD)?.let {
            sensorManager.registerListener(this, it, SensorManager.SENSOR_DELAY_UI)
        }
    }

    fun stopCompass() {
        sensorManager.unregisterListener(this)
    }

    override fun onSensorChanged(event: SensorEvent) {
        when (event.sensor.type) {
            Sensor.TYPE_ACCELEROMETER ->
                System.arraycopy(event.values, 0, accelerometerReading, 0, 3)
            Sensor.TYPE_MAGNETIC_FIELD ->
                System.arraycopy(event.values, 0, magnetometerReading, 0, 3)
            else -> return
        }
        SensorManager.getRotationMatrix(rotationMatrix, null, accelerometerReading, magnetometerReading)
        SensorManager.getOrientation(rotationMatrix, orientationAngles)
        val azimuthDegrees = Math.toDegrees(orientationAngles[0].toDouble()).toFloat()
        val normalised = (azimuthDegrees + 360f) % 360f
        _uiState.update { it.copy(compassDegree = normalised) }
    }

    override fun onAccuracyChanged(sensor: Sensor, accuracy: Int) {
        if (sensor.type == Sensor.TYPE_MAGNETIC_FIELD)
            _uiState.update { it.copy(accuracy = accuracy) }
    }

    override fun onCleared() {
        stopCompass()
        super.onCleared()
    }
}
