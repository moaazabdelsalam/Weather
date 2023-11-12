package com.project.weather.network

import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.WeatherResponse
import retrofit2.Response

interface RemoteSource {
    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse>

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Response<ReverseNominationResponse>
}