package com.project.weather.model

import java.io.Serializable

data class Weather(
    val id: Long,
    val main: String,
    val description: String,
    val icon: String
) : Serializable
