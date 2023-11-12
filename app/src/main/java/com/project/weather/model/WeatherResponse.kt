package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class WeatherResponse(
    val lat: Double,
    val lon: Double,
    var timezone: String,
    @SerializedName("timezone_offset")
    val timezoneOffset: Long,
    val current: Current,
    val hourly: List<Hourly>,
    val daily: List<Daily>,
    var nameAr: String,
    var nameEn: String
) : java.io.Serializable
