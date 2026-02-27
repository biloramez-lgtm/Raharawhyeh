package com.raharawhey.data.receiver

import android.app.AlarmManager
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.raharawhey.RahaRawheyApp
import com.raharawhey.data.SettingsDataStore
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import java.util.concurrent.Executors

// ─────────────────────────────────────────────────────────────────────────────
// PrayerAlarmReceiver — يُستدعى عند حلول وقت الصلاة
// ─────────────────────────────────────────────────────────────────────────────
class PrayerAlarmReceiver : BroadcastReceiver() {

    @EntryPoint
    @InstallIn(SingletonComponent::class)
    interface PrayerAlarmReceiverEntryPoint {
        fun settingsDataStore(): SettingsDataStore
    }

    override fun onReceive(context: Context, intent: Intent) {
        val prayerName = intent.getStringExtra(EXTRA_PRAYER_NAME)  ?: "الصلاة"
        val prayerTime = intent.getStringExtra(EXTRA_PRAYER_TIME)  ?: ""
        val notifId    = intent.getIntExtra(EXTRA_REQUEST_CODE, prayerName.hashCode())

        val entryPoint = EntryPointAccessors.fromApplication(context, PrayerAlarmReceiverEntryPoint::class.java)
        val vibrationEnabled = runBlocking { entryPoint.settingsDataStore().vibrationEnabled.first() }

        val channelId = if (vibrationEnabled) {
            RahaRawheyApp.CHANNEL_PRAYER_VIBRATE
        } else {
            RahaRawheyApp.CHANNEL_PRAYER_NO_VIBRATE
        }

        val notification = NotificationCompat.Builder(context, channelId)
            .setSmallIcon(android.R.drawable.ic_dialog_info) // TODO: Change to R.drawable.ic_mosque
            .setContentTitle("حان وقت $prayerName 🕌")
            .setContentText(if (prayerTime.isNotEmpty()) "الوقت: $prayerTime" else "استعد للصلاة")
            .setStyle(
                NotificationCompat.BigTextStyle()
                    .bigText("حان وقت صلاة $prayerName\nالوقت: $prayerTime\nاللهم اجعلنا من المصلين")
            )
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_ALARM)
            .setAutoCancel(true)
            .build()

        val nm = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        nm.notify(notifId, notification)
    }

    companion object {
        const val EXTRA_PRAYER_NAME  = "prayer_name"
        const val EXTRA_PRAYER_TIME  = "prayer_time"
        const val EXTRA_REQUEST_CODE = "request_code"

        const val ID_FAJR    = 1001
        const val ID_DHUHR   = 1002
        const val ID_ASR     = 1003
        const val ID_MAGHRIB = 1004
        const val ID_ISHA    = 1005

        fun schedule(
            context: Context,
            prayerName: String,
            prayerTime: String,
            triggerAtMillis: Long,
            requestCode: Int
        ) {
            val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as AlarmManager

            val intent = Intent(context, PrayerAlarmReceiver::class.java).apply {
                putExtra(EXTRA_PRAYER_NAME,  prayerName)
                putExtra(EXTRA_PRAYER_TIME,  prayerTime)
                putExtra(EXTRA_REQUEST_CODE, requestCode)
            }

            val pendingIntent = PendingIntent.getBroadcast(
                context,
                requestCode,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )

            alarmManager.setExactAndAllowWhileIdle(
                AlarmManager.RTC_WAKEUP,
                triggerAtMillis,
                pendingIntent
            )
        }

        fun scheduleAllPrayers(
            context: Context,
            fajrMillis: Long,    fajrTime: String    = "",
            dhuhrMillis: Long,   dhuhrTime: String   = "",
            asrMillis: Long,     asrTime: String     = "",
            maghribMillis: Long, maghribTime: String = "",
            ishaMillis: Long,    ishaTime: String    = ""
        ) {
            val now = System.currentTimeMillis()
            if (fajrMillis    > now) schedule(context, "الفجر",  fajrTime,    fajrMillis,    ID_FAJR)
            if (dhuhrMillis   > now) schedule(context, "الظهر",  dhuhrTime,   dhuhrMillis,   ID_DHUHR)
            if (asrMillis     > now) schedule(context, "العصر",  asrTime,     asrMillis,     ID_ASR)
            if (maghribMillis > now) schedule(context, "المغرب", maghribTime, maghribMillis, ID_MAGHRIB)
            if (ishaMillis    > now) schedule(context, "العشاء", ishaTime,    ishaMillis,    ID_ISHA)
        }
    }
}

class BootReceiver : BroadcastReceiver() {

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action != Intent.ACTION_BOOT_COMPLETED) return

        val pendingResult = goAsync()
        Executors.newSingleThreadExecutor().execute {
            try {
                val prefs = context.getSharedPreferences("prayer_cache", Context.MODE_PRIVATE)
                val fajrMs = prefs.getLong("fajr_ms", 0L)
                if (fajrMs > 0L) {
                    PrayerAlarmReceiver.scheduleAllPrayers(
                        context = context,
                        fajrMillis = fajrMs,
                        dhuhrMillis = prefs.getLong("dhuhr_ms", 0L),
                        asrMillis = prefs.getLong("asr_ms", 0L),
                        maghribMillis = prefs.getLong("maghrib_ms", 0L),
                        ishaMillis = prefs.getLong("isha_ms", 0L)
                    )
                }
            } finally {
                pendingResult.finish()
            }
        }
    }
}
