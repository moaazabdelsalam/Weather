package com.project.weather.repo

import com.project.weather.model.WeatherResponse

interface RepoInterface {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherResponse
}