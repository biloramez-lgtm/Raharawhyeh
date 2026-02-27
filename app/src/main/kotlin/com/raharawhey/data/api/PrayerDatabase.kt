package com.raharawhey.data.api

import androidx.room.*
import com.raharawhey.data.models.PrayerTimesCache

@Dao
interface PrayerTimesDao {
    @Query("SELECT * FROM prayer_times_cache WHERE dateKey = :dateKey AND latitude BETWEEN :lat - 0.01 AND :lat + 0.01 AND longitude BETWEEN :lng - 0.01 AND :lng + 0.01 AND method = :method")
    suspend fun getCachedTimes(dateKey: String, lat: Double, lng: Double, method: Int): PrayerTimesCache?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCache(cache: PrayerTimesCache)

    @Query("DELETE FROM prayer_times_cache WHERE cachedAt < :threshold")
    suspend fun deleteOldCache(threshold: Long)
}

@Database(entities = [PrayerTimesCache::class], version = 4, exportSchema = false)
abstract class PrayerDatabase : RoomDatabase() {
    abstract fun prayerTimesDao(): PrayerTimesDao

    companion object {
        const val DATABASE_NAME = "raha_rawhey_db"
    }
}
