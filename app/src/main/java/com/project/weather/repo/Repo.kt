package com.project.weather.repo

import android.util.Log
import com.project.weather.constants.Constants
import com.project.weather.model.ApiState
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

class Repo private constructor(private val remoteSource: RemoteSource) : RepoInterface {
    val TAG = "TAG Repo"

    companion object {
        @Volatile
        private var _instance: Repo? = null
        fun getInstance(remoteSource: RemoteSource): Repo {
            return _instance ?: synchronized(this) {
                val instance = Repo(remoteSource)
                _instance = instance
                instance
            }
        }
    }

    override suspend fun getWeatherDataOfLocation(
        latitude: Double,
        longitude: Double
    ): Flow<ApiState> {
        return flow<ApiState> {
            val cachedWeatherData = readCachedWeatherData()
            if (cachedWeatherData != null) {
                //Log.i(TAG, "cached data: ${cachedWeatherData.current.temp}")
                emit(ApiState.Successful(cachedWeatherData))
            }
            emit(ApiState.Loading)
            try {
                val result = remoteSource.getWeatherData(latitude, longitude)
                if (result.isSuccessful) {
                    emit(ApiState.Successful(result.body()))
                    cacheWeatherData(result.body())
                } else {
                    emit(ApiState.Failure(result.message()))
                }
            } catch (e: Exception) {
                emit(ApiState.Failure(e.message.toString()))
            }
        }
    }

    private suspend fun cacheWeatherData(weatherData: WeatherResponse?) {
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

    private suspend fun readCachedWeatherData(): WeatherResponse? {
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
}
