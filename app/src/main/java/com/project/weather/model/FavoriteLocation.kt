package com.project.weather.model

data class FavoriteLocation(
    val timezone: String,
    val main: String,
    val description: String,
    val icon: String,
    val min: Double,
    val max: Double,
)
