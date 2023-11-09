package com.project.weather.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import org.jetbrains.annotations.NotNull

@Entity(tableName = "favorite_table")
data class FavoriteLocation(
    var lat: Double,
    var lon: Double,
    @PrimaryKey
    var timezone: String,
    var main: String,
    var description: String,
    var icon: String,
    var min: Double,
    var max: Double,
)
