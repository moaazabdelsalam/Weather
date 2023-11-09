package com.project.weather.repo

import com.project.weather.model.ApiState
import com.project.weather.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getWeatherDataOfLocation(
        latitude: Double,
        longitude: Double
    ): Flow<ApiState>

    suspend fun getWeatherDataAndAddToFavorite(latitude: Double, longitude: Double): Long
    suspend fun addToFavorite(location: FavoriteLocation): Long

    suspend fun deleteFromFavorite(location: FavoriteLocation): Int

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
}