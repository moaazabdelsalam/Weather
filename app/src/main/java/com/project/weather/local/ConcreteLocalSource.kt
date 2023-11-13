package com.project.weather.local

import android.content.Context
import android.util.Log
import com.project.weather.constants.Constants
import com.project.weather.local.database.FavoriteDAO
import com.project.weather.local.database.FavoriteDatabase
import com.project.weather.model.AlertItem
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class ConcreteLocalSource private constructor(context: Context) : LocalSource {
    private val TAG = "TAG LocalSource"

    private var favoriteDatabase: FavoriteDatabase = FavoriteDatabase.getInstance(context)
    private var favoriteDAO: FavoriteDAO = favoriteDatabase.getFavoriteDao()

    companion object {
        @Volatile
        private var _instance: ConcreteLocalSource? = null
        fun getInstance(context: Context): ConcreteLocalSource {
            return _instance ?: synchronized(this) {
                val instance = ConcreteLocalSource(context)
                _instance = instance
                instance
            }
        }
    }

    override suspend fun addToFavorite(location: FavoriteLocation) =
        favoriteDAO.addToFavorite(location)

    override suspend fun deleteFromFavorite(location: FavoriteLocation) =
        favoriteDAO.deleteFromFavorite(location)

    override fun getAllFavoriteLocations() = favoriteDAO.getAllFavoriteLocations()

    override suspend fun cacheWeatherData(weatherData: WeatherResponse?) {
        val file = File(Constants.cacheDirectory, Constants.CACHE_FILE_NAME)
        withContext(Dispatchers.IO) {
            val fileOutputStream = FileOutputStream(file)
            val objectOutputStream = ObjectOutputStream(fileOutputStream)
            try {
                objectOutputStream.writeObject(weatherData)
                objectOutputStream.flush()
                fileOutputStream.flush()
                objectOutputStream.close()
                fileOutputStream.close()
                Log.i(TAG, "cacheWeatherData: finished")
            } catch (e: Exception) {
                Log.i("TAG", "caching exception: $e")
            }
        }
    }

    override suspend fun readCachedWeatherData(): WeatherResponse? {
        val file = File(Constants.cacheDirectory, Constants.CACHE_FILE_NAME)
        return try {
            withContext(Dispatchers.IO) {
                val fileInputStream = FileInputStream(file)
                val objectInputStream = ObjectInputStream(fileInputStream)
                val weatherData = objectInputStream.readObject() as WeatherResponse
                Log.i(TAG, "readCachedWeatherData: success")
                weatherData
            }
        } catch (e: Exception) {
            Log.i(TAG, "readCachedWeatherData exception: $e")
            null
        }
    }

    override suspend fun updateAlert(location: FavoriteLocation) = favoriteDAO.updateAlert(location)

}