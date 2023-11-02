package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class Hourly(
    val dt: Long,
    val temp: Double,
    @SerializedName("feels_like")
    val feelsLike: Double,
    val pressure: Long,
    val humidity: Long,
    @SerializedName("dew_point")
    val dewPoint: Double,
    val uvi: Double,
    val clouds: Long,
    val visibility: Long,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Long,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<Weather>,
    val pop: Long
)
