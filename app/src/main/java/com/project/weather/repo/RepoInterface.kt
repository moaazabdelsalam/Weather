package com.project.weather.repo

import com.project.weather.model.FavoriteLocation
import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

interface RepoInterface {
    val homeDataApiState: SharedFlow<State<WeatherResponse?>>
    suspend fun getWeatherDataOfHomeLocation(
        latitude: Double,
        longitude: Double
    )

    suspend fun getCachedWeatherDataAndUpdate()

    suspend fun setHomeLocation(latitude: Double, longitude: Double)

    suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Flow<State<WeatherResponse?>>

    suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Flow<State<ReverseNominationResponse>>

    suspend fun getCachedWeatherData(): State<WeatherResponse?>

    suspend fun addToFavorite(location: FavoriteLocation): Long

    suspend fun deleteFromFavorite(location: FavoriteLocation): Int

    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>

    suspend fun updateAlert(location: FavoriteLocation)
}