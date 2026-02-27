package com.raharawhey.data.models

import androidx.room.Entity
import androidx.room.PrimaryKey

// ─── Prayer Time Model ────────────────────────────────────────────────────────
data class PrayerTimesResponse(
    val code: Int,
    val status: String,
    val data: PrayerData
)

data class PrayerData(
    val timings: Timings,
    val date: DateInfo,
    val meta: MetaInfo
)

data class Timings(
    val Fajr: String,
    val Sunrise: String,
    val Dhuhr: String,
    val Asr: String,
    val Sunset: String,
    val Maghrib: String,
    val Isha: String,
    val Imsak: String,
    val Midnight: String,
    val Firstthird: String,
    val Lastthird: String
)

data class DateInfo(
    val readable: String,
    val timestamp: String,
    val hijri: HijriDate,
    val gregorian: GregorianDate
)

data class HijriDate(
    val date: String,
    val format: String,
    val day: String,
    val weekday: HijriWeekday,
    val month: HijriMonth,
    val year: String
)

data class HijriWeekday(val en: String, val ar: String)
data class HijriMonth(val number: Int, val en: String, val ar: String)
data class GregorianDate(val date: String, val format: String, val day: String, val weekday: GregorianWeekday, val month: GregorianMonth, val year: String, val designation: Designation)
data class GregorianWeekday(val en: String)
data class GregorianMonth(val number: Int, val en: String)
data class Designation(val abbreviated: String, val expanded: String)

data class MetaInfo(
    val latitude: Double,
    val longitude: Double,
    val timezone: String,
    val method: CalculationMethod,
    val latitudeAdjustmentMethod: String,
    val midnightMode: String,
    val school: String,
    val offset: Map<String, Int>
)

data class CalculationMethod(
    val id: Int,
    val name: String,
    val params: Map<String, Any>,
    val location: MethodLocation?
)

data class MethodLocation(val latitude: Double, val longitude: Double)

// ─── Prayer Entry ─────────────────────────────────────────────────────────────
data class PrayerEntry(
    val name: String,
    val nameAr: String,
    val time: String,          // "05:23"
    val isNext: Boolean = false,
    val isPassed: Boolean = false,
    val icon: String = ""
)

// ─── Cached Prayer Times (Room) ───────────────────────────────────────────────
@Entity(tableName = "prayer_times_cache", primaryKeys = ["dateKey", "latitude", "longitude", "method"])
data class PrayerTimesCache(
    val dateKey: String,   // "2024-01-15"
    val latitude: Double,
    val longitude: Double,
    val method: Int,
    val timezone: String,
    val fajr: String,
    val sunrise: String,
    val dhuhr: String,
    val asr: String,
    val maghrib: String,
    val isha: String,
    val hijriDate: String,
    val hijriMonth: String,
    val hijriYear: String,
    val cachedAt: Long = System.currentTimeMillis()
)

// ─── User Preferences ─────────────────────────────────────────────────────────
data class UserPreferences(
    val calculationMethod: Int = 4,         // 4 = Umm Al-Qura
    val asrMethod: Int = 0,                 // 0 = Shafi'i, 1 = Hanafi
    val latitude: Double = 21.3891,         // Makkah default
    val longitude: Double = 39.8579,
    val cityName: String = "مكة المكرمة",
    val notificationsEnabled: Boolean = true,
    val adhanEnabled: Boolean = true,
    val language: String = "ar"
)

// ─── Calculation Methods ──────────────────────────────────────────────────────
enum class CalculationMethodEnum(
    val id: Int,
    val nameAr: String,
    val nameEn: String
) {
    MWL(3, "رابطة العالم الإسلامي", "Muslim World League"),
    EGYPTIAN(5, "الهيئة المصرية", "Egyptian General Authority"),
    KARACHI(1, "جامعة كراتشي", "University of Islamic Sciences, Karachi"),
    UMM_AL_QURA(4, "أم القرى", "Umm Al-Qura University, Makkah"),
    DUBAI(16, "دبي", "Dubai (unofficial)"),
    QATAR(10, "قطر", "Qatar"),
    KUWAIT(9, "الكويت", "Kuwait"),
    GULF(8, "منطقة الخليج", "Gulf Region"),
    TURKEY(13, "تركيا", "Diyanet İşleri Başkanlığı, Turkey"),
    TEHRAN(7, "طهران", "Institute of Geophysics, Tehran"),
    ISNA(2, "أمريكا الشمالية", "Islamic Society of North America"),
    FRANCE(12, "فرنسا", "Union des Organisations Islamiques de France"),
    RUSSIA(14, "روسيا", "Spiritual Administration of Muslims of Russia"),
    SINGAPORE(11, "سنغافورة", "Majlis Ugama Islam Singapura")
}
