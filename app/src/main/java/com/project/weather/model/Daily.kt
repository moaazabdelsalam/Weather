package com.project.weather.model

import com.google.gson.annotations.SerializedName
import java.io.Serializable

data class Daily(
    val dt: Long,
    val sunrise: Long,
    val sunset: Long,
    val moonrise: Long,
    @SerializedName("moonset")
    val moonSet: Long,
    @SerializedName("moon_phase")
    val moonPhase: Double,
    val temp: Temperature,
    @SerializedName("feels_like")
    val feelsLike: FeelsLike,
    val pressure: Long,
    val humidity: Long,
    @SerializedName("dew_point")
    val dewPoint: Double,
    @SerializedName("wind_speed")
    val windSpeed: Double,
    @SerializedName("wind_deg")
    val windDeg: Long,
    @SerializedName("wind_gust")
    val windGust: Double,
    val weather: List<Weather>,
    val clouds: Long,
    val pop: Double,
    val uvi: Double
) : Serializable
