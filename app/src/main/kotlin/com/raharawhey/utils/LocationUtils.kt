package com.raharawhey.utils

import android.annotation.SuppressLint
import android.content.Context
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import com.google.android.gms.tasks.CancellationTokenSource
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.math.*

object LocationUtils {

    @SuppressLint("MissingPermission")
    suspend fun getCurrentLocation(context: Context): Pair<Double, Double>? =
        suspendCancellableCoroutine { cont ->
            val client = LocationServices.getFusedLocationProviderClient(context)
            val cts = CancellationTokenSource()
            client.getCurrentLocation(Priority.PRIORITY_BALANCED_POWER_ACCURACY, cts.token)
                .addOnSuccessListener { loc ->
                    cont.resume(if (loc != null) Pair(loc.latitude, loc.longitude) else null)
                }
                .addOnFailureListener { cont.resume(null) }
            cont.invokeOnCancellation { cts.cancel() }
        }

    /**
     * Calculate Qibla direction (bearing) from given location to Makkah
     * Returns bearing in degrees from North (0-360)
     */
    fun calculateQiblaDirection(lat: Double, lng: Double): Float {
        val makkahLat = Math.toRadians(21.4225)
        val makkahLng = Math.toRadians(39.8262)
        val userLat   = Math.toRadians(lat)
        val userLng   = Math.toRadians(lng)

        val dLng = makkahLng - userLng
        val y = sin(dLng) * cos(makkahLat)
        val x = cos(userLat) * sin(makkahLat) - sin(userLat) * cos(makkahLat) * cos(dLng)

        var bearing = Math.toDegrees(atan2(y, x)).toFloat()
        bearing = (bearing + 360f) % 360f
        return bearing
    }

    /**
     * Calculate distance to Makkah in km
     */
    fun distanceToMakkah(lat: Double, lng: Double): Double {
        val R = 6371.0
        val mLat = Math.toRadians(21.4225)
        val mLng = Math.toRadians(39.8262)
        val uLat = Math.toRadians(lat)
        val uLng = Math.toRadians(lng)
        val dLat = mLat - uLat
        val dLng = mLng - uLng
        val a = sin(dLat/2).pow(2) + cos(uLat) * cos(mLat) * sin(dLng/2).pow(2)
        val c = 2 * atan2(sqrt(a), sqrt(1-a))
        return R * c
    }
}
