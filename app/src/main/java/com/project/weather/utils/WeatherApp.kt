package com.project.weather.utils

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.graphics.Typeface
import android.os.Build
import androidx.appcompat.app.AppCompatDelegate
import androidx.core.content.res.ResourcesCompat
import com.project.weather.R
import com.project.weather.constants.Constants
import com.yariksoffice.lingver.Lingver

class WeatherApp : Application() {
    override fun onCreate() {
        super.onCreate()
        Lingver.init(this)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val forecastChannel = NotificationChannel(
                Constants.FORECAST_NOTIFICATION_CHANNEL_ID,
                "Forecast State",
                NotificationManager.IMPORTANCE_LOW
            )
            forecastChannel.description =
                "This is an notification that gives you an update with weather state"

            val alertsChannel = NotificationChannel(
                Constants.ALERTS_NOTIFICATION_CHANNEL_ID,
                "Weather Alerts",
                NotificationManager.IMPORTANCE_HIGH
            )
            alertsChannel.description =
                "get alerts for any weather events"
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(forecastChannel)
            notificationManager.createNotificationChannel(alertsChannel)
        }
    }
}