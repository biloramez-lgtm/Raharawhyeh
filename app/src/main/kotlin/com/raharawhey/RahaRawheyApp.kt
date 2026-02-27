package com.raharawhey

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.ContentResolver
import android.media.AudioAttributes
import android.net.Uri
import dagger.hilt.android.HiltAndroidApp

@HiltAndroidApp
class RahaRawheyApp : Application() {

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

        val adhanSound = Uri.parse("${ContentResolver.SCHEME_ANDROID_RESOURCE}://${packageName}/${R.raw.azan1}")

        val audioAttributes = AudioAttributes.Builder()
            .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
            .setUsage(AudioAttributes.USAGE_ALARM)
            .build()

        // Channel WITH vibration
        val prayerChannelVibrate = NotificationChannel(
            CHANNEL_PRAYER_VIBRATE,
            "أوقات الصلاة (باهتزاز)",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "تنبيه عند دخول وقت الصلاة مع اهتزاز"
            enableVibration(true)
            setSound(adhanSound, audioAttributes)
        }

        // Channel WITHOUT vibration
        val prayerChannelNoVibrate = NotificationChannel(
            CHANNEL_PRAYER_NO_VIBRATE,
            "أوقات الصلاة (صامت)",
            NotificationManager.IMPORTANCE_HIGH
        ).apply {
            description = "تنبيه عند دخول وقت الصلاة بدون اهتزاز"
            enableVibration(false)
            setSound(adhanSound, audioAttributes)
        }

        manager.createNotificationChannel(prayerChannelVibrate)
        manager.createNotificationChannel(prayerChannelNoVibrate)
    }

    companion object {
        const val CHANNEL_PRAYER_VIBRATE = "prayer_times_vibrate"
        const val CHANNEL_PRAYER_NO_VIBRATE = "prayer_times_no_vibrate"
    }
}
