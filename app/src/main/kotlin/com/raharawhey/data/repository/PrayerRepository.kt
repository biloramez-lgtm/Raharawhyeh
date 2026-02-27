package com.raharawhey.data.repository

import com.raharawhey.data.api.PrayerApiService
import com.raharawhey.data.api.PrayerTimesDao
import com.raharawhey.data.models.PrayerEntry
import com.raharawhey.data.models.PrayerTimesCache
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import java.text.SimpleDateFormat
import java.time.LocalTime
import java.time.ZoneId
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter
import java.util.*
import javax.inject.Inject
import javax.inject.Singleton

sealed class Result<out T> {
    data class Success<T>(val data: T) : Result<T>()
    data class Error(val message: String, val exception: Exception? = null) : Result<Nothing>()
    object Loading : Result<Nothing>()
}

@Singleton
class PrayerRepository @Inject constructor(
    private val api: PrayerApiService,
    private val dao: PrayerTimesDao
) {
    private val dateFormat = SimpleDateFormat("dd-MM-yyyy", Locale.getDefault())

    fun getPrayerTimes(
        lat: Double,
        lng: Double,
        method: Int = 4,
        school: Int = 0
    ): Flow<Result<PrayerTimesCache>> = flow {
        emit(Result.Loading)

        val today = dateFormat.format(Date())
        val cached = dao.getCachedTimes(today, lat, lng, method)
        if (cached != null) {
            emit(Result.Success(cached))
            return@flow
        }

        try {
            val timestamp = System.currentTimeMillis() / 1000
            val response = api.getPrayerTimesByCoordinates(timestamp, lat, lng, method, school)
            if (response.isSuccessful) {
                val body = response.body()!!
                val t = body.data.timings
                val h = body.data.date.hijri
                val m = body.data.meta
                val cache = PrayerTimesCache(
                    dateKey = today,
                    latitude = lat,
                    longitude = lng,
                    method = method,
                    timezone = m.timezone,
                    fajr = t.Fajr,
                    sunrise = t.Sunrise,
                    dhuhr = t.Dhuhr,
                    asr = t.Asr,
                    maghrib = t.Maghrib,
                    isha = t.Isha,
                    hijriDate = h.day,
                    hijriMonth = h.month.ar,
                    hijriYear = h.year
                )
                dao.insertCache(cache)
                val weekAgo = System.currentTimeMillis() - 7 * 24 * 60 * 60 * 1000L
                dao.deleteOldCache(weekAgo)
                emit(Result.Success(cache))
            } else {
                emit(Result.Error("خطأ في الخادم: ${response.code()}"))
            }
        } catch (e: Exception) {
            emit(Result.Error("خطأ في الاتصال بالإنترنت", e))
        }
    }

    fun buildPrayerEntries(cache: PrayerTimesCache): List<PrayerEntry> {
        val prayerZoneId = ZoneId.of(cache.timezone)
        val nowInPrayerZone = ZonedDateTime.now(prayerZoneId)
        val currentTime = nowInPrayerZone.toLocalTime()
        val apiTimeParser = DateTimeFormatter.ofPattern("HH:mm")

        val prayersSource = listOf(
            PrayerEntry(name = "Fajr", nameAr = "الفجر", time = cache.fajr, icon = "🌙"),
            PrayerEntry(name = "Sunrise", nameAr = "الشروق", time = cache.sunrise, icon = "🌅"),
            PrayerEntry(name = "Dhuhr", nameAr = "الظهر", time = cache.dhuhr, icon = "☀️"),
            PrayerEntry(name = "Asr", nameAr = "العصر", time = cache.asr, icon = "🌤️"),
            PrayerEntry(name = "Maghrib", nameAr = "المغرب", time = cache.maghrib, icon = "🌆"),
            PrayerEntry(name = "Isha", nameAr = "العشاء", time = cache.isha, icon = "🌙")
        )

        var nextPrayerFound = false
        val processedPrayers = prayersSource.map { prayerInfo ->
            try {
                val prayerLocalTime = LocalTime.parse(prayerInfo.time, apiTimeParser)
                val isPassed = currentTime.isAfter(prayerLocalTime)

                val isNext = !isPassed && !nextPrayerFound
                if (isNext) {
                    nextPrayerFound = true
                }

                prayerInfo.copy(isPassed = isPassed, isNext = isNext)
            } catch (e: Exception) {
                prayerInfo
            }
        }

        // Special Case: If all prayers for today have passed, the next prayer is Fajr tomorrow.
        if (!nextPrayerFound) {
            return processedPrayers.mapIndexed { index, prayer ->
                if (index == 0) {
                    // Fajr is the next prayer, and it has NOT passed (in context of the new day cycle)
                    prayer.copy(isNext = true, isPassed = false)
                } else {
                    // All other prayers of the day are now in the past.
                    prayer.copy(isNext = false, isPassed = true)
                }
            }
        }

        return processedPrayers
    }
}
