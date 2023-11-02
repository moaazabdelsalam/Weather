package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    val timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Long,
    val current: Current,
    val minutely: List<Minutely>,
    val hourly: List<Hourly>,
    val daily: List<Daily>
)
