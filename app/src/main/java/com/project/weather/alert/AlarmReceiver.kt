package com.project.weather.alert

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.graphics.BitmapFactory
import android.media.RingtoneManager
import android.util.Log
import androidx.core.app.NotificationCompat
import com.project.weather.MainActivity
import com.project.weather.R
import com.project.weather.constants.Constants
import com.project.weather.local.ConcreteLocalSource
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo
import com.project.weather.repo.RepoInterface
import com.project.weather.utils.convertWeatherToFavorite
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlin.coroutines.CoroutineContext
import kotlin.coroutines.EmptyCoroutineContext

class AlarmReceiver : BroadcastReceiver() {
    private val TAG = "TAG AlarmReceiver"
    lateinit var repo: RepoInterface
    val data: MutableStateFlow<WeatherResponse?> = MutableStateFlow(null)

    override fun onReceive(context: Context?, intent: Intent?) = goAsync {
        intent?.let {
            val cityName = it.getStringExtra(Constants.EXTRA_CITY_NAME_KEY) ?: return@let
            val lat = it.getStringExtra(Constants.EXTRA_LAT_KEY)?.toDouble() ?: return@let
            val lon = it.getStringExtra(Constants.EXTRA_LON_KEY)?.toDouble() ?: return@let
            Log.i(TAG, "onAlarmReceive: $cityName, $lat, $lon")
            if (context != null) {
                getWeatherData(context, lat, lon)
                when (data.value) {
                    null -> {}

                    else -> {
                        data.value?.let { weatherResponse ->
                            showNotification(context, cityName, weatherResponse)
                            val location = convertWeatherToFavorite(weatherResponse, cityName)
                            repo.updateAlert(location)
                        }
                    }
                }
            }
        }
    }

    private suspend fun getWeatherData(
        appContext: Context,
        lat: Double,
        lon: Double
    ) {
        Log.i(TAG, "getWeatherData: ")
        repo = Repo.getInstance(
            WeatherClient,
            ConcreteLocalSource.getInstance(appContext)
        )
        repo.getWeatherData(lat, lon).collectLatest { state ->
            when (state) {
                is State.Failure -> {
                    Log.i(TAG, "getWeatherData failure: ${state.error}")
                }

                State.Loading -> {}

                is State.Success -> {
                    if (state.data != null) {
                        Log.i(TAG, "getWeatherData success")
                        data.value = state.data
                    }
                }
            }
        }
    }

    private fun showNotification(
        appContext: Context,
        cityName: String,
        weatherResponse: WeatherResponse
    ) {
        Log.i(TAG, "showNotification: ")
        val notificationManager =
            appContext.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val intent = Intent(appContext, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }
        val pendingIntent: PendingIntent =
            PendingIntent.getActivities(
                appContext,
                0,
                arrayOf(intent),
                PendingIntent.FLAG_IMMUTABLE
            )
        val largeIcon = BitmapFactory.decodeResource(appContext.resources, R.drawable.app_icon)
        val notification =
            NotificationCompat.Builder(appContext, Constants.ALERTS_NOTIFICATION_CHANNEL_ID)
                .setSmallIcon(R.drawable.icon)
                .setContentTitle("Alert in $cityName")
                .setContentText(
                    weatherResponse.alerts?.get(0)?.tags?.get(0)
                        ?: "No weather alerts for now"
                )
                .setStyle(
                    NotificationCompat.BigTextStyle()
                        .bigText(
                            weatherResponse.alerts?.get(0)?.description
                                ?: ("No weather alerts for now in $cityName\n" +
                                        " Current forecast is \"${weatherResponse.current.weather[0].description}\"" +
                                        " and temp ${weatherResponse.current.temp.toInt()}℃")
                        )
                        .setBigContentTitle(
                            weatherResponse.alerts?.get(0)?.tags?.get(0)
                                ?: "No weather alerts for now"
                        )
                        .setSummaryText("Alert in $cityName")
                )
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setDefaults(NotificationCompat.DEFAULT_ALL)
                .setContentIntent(pendingIntent)
                .setAutoCancel(true)
                .setSound(RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION))
                .setLargeIcon(largeIcon)
                .build()
        notificationManager.notify(Constants.ALERTS_NOTIFICATION_ID, notification)
    }
}

fun BroadcastReceiver.goAsync(
    context: CoroutineContext = EmptyCoroutineContext,
    block: suspend CoroutineScope.() -> Unit
) {
    val pendingResult = goAsync()
    @OptIn(DelicateCoroutinesApi::class)
    GlobalScope.launch(context) {
        try {
            block()
        } finally {
            pendingResult.finish()
            Log.i("TAG", "goAsync: finished")
        }
    }
}