package com.project.weather.model

import androidx.room.Entity

@Entity(tableName = "favorite_table", primaryKeys = ["lat", "lon"])
data class FavoriteLocation(
    var lat: Double,
    var lon: Double,
    var cityName: String,
    var main: String,
    var description: String,
    var icon: String,
    var min: Double,
    var max: Double,
    var timeString: String = "",
    var isScheduled: Boolean = false
)
