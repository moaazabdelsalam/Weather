package com.project.weather.local.database

import com.project.weather.model.FavoriteLocation
import kotlinx.coroutines.flow.Flow

interface LocalSource {
    suspend fun addToFavorite(location: FavoriteLocation): Long
    suspend fun deleteFromFavorite(location: FavoriteLocation): Int
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
}