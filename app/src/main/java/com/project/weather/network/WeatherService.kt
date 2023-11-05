package com.project.weather.network

import com.project.weather.model.WeatherResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface WeatherService {

    @GET("onecall")
    suspend fun getOneCallData(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double
    ): Response<WeatherResponse>
}