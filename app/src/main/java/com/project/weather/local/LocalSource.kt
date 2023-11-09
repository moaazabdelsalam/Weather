package com.project.weather.local

import android.util.Log
import com.project.weather.constants.Constants
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

interface LocalSource {
    suspend fun addToFavorite(location: FavoriteLocation): Long
    suspend fun deleteFromFavorite(location: FavoriteLocation): Int
    fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>>
    suspend fun cacheWeatherData(weatherData: WeatherResponse?)
    suspend fun readCachedWeatherData(): WeatherResponse?
}