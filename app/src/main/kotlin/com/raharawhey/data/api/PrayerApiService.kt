package com.raharawhey.data.api

import com.raharawhey.data.models.PrayerTimesResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface PrayerApiService {

    /**
     * Get prayer times by coordinates
     * API: https://aladhan.com/prayer-times-api
     */
    @GET("v1/timings/{timestamp}")
    suspend fun getPrayerTimesByCoordinates(
        @Path("timestamp") timestamp: Long,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4,
        @Query("school") school: Int = 0,
        @Query("tune") tune: String = "0,0,0,0,0,0,0,0,0"
    ): Response<PrayerTimesResponse>

    /**
     * Get prayer times by city name
     */
    @GET("v1/timingsByCity/{timestamp}")
    suspend fun getPrayerTimesByCity(
        @Path("timestamp") timestamp: Long,
        @Query("city") city: String,
        @Query("country") country: String,
        @Query("method") method: Int = 4,
        @Query("school") school: Int = 0
    ): Response<PrayerTimesResponse>

    /**
     * Get monthly prayer times
     */
    @GET("v1/calendar/{year}/{month}")
    suspend fun getMonthlyCalendar(
        @Path("year") year: Int,
        @Path("month") month: Int,
        @Query("latitude") latitude: Double,
        @Query("longitude") longitude: Double,
        @Query("method") method: Int = 4
    ): Response<Map<String, Any>>
}

object ApiConstants {
    const val BASE_URL = "https://api.aladhan.com/"
    const val TIMEOUT_SECONDS = 30L
}
