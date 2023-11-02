package com.project.weather.network

import com.project.weather.model.WeatherResponse

interface RemoteSource {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherResponse
}