package com.project.weather.network

import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.WeatherResponse
import retrofit2.Response

object WeatherClient : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse> {
        return API.weatherService.getOneCallData(latitude, longitude)
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Response<ReverseNominationResponse> {
        return API.nominationService.getCityName(latitude, longitude)
    }
}