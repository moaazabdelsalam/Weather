package com.project.weather.network

import com.project.weather.model.WeatherResponse
import retrofit2.Response

object WeatherClient : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse> {
        return API.retrofitService.getOneCallData(latitude, longitude)
    }
}