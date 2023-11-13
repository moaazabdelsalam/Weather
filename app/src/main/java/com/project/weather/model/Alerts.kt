package com.project.weather.model

data class Alerts(
    val event: String,
    val start: Long,
    val end: Long,
    val description: String,
    val tags: List<String>
)
