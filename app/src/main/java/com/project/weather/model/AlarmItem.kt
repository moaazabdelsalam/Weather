package com.project.weather.model

import java.time.LocalDateTime

data class AlarmItem(
    val time: LocalDateTime,
    val cityName: String,
    val lat: Double,
    val lon: Double
)
