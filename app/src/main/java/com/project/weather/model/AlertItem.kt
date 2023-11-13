package com.project.weather.model

import java.time.LocalDateTime

data class AlertItem(
    var time: LocalDateTime,
    var cityName: String,
    var lat: Double,
    var lon: Double
)
