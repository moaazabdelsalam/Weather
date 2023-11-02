package com.project.weather.network

import com.project.weather.model.WeatherResponse

object WeatherClient : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherResponse {
        return API.retrofitService.getOneCallData(latitude, longitude)
    }
}