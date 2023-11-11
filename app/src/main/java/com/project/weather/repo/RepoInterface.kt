package com.project.weather.repo

import com.project.weather.model.State
import com.project.weather.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getWeatherDataOfHomeLocation(
        latitude: Double,
        longitude: Double
    ): Flow<State>

    suspend fun getWeatherData(latitude: Double, longitude: Double): Flow<State>

    suspend fun addToFavorite(location: FavoriteLocation): Long

    suspend fun deleteFromFavorite(location: FavoriteLocation): Int

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
}