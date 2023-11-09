package com.project.weather.repo

import android.util.Log
import com.project.weather.local.LocalSource
import com.project.weather.model.ApiState
import com.project.weather.model.FavoriteLocation
import com.project.weather.network.RemoteSource
import com.project.weather.utils.convertWeatherToFavorite
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {
    val TAG = "TAG Repo"

    companion object {
        @Volatile
        private var _instance: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repo {
            return _instance ?: synchronized(this) {
                val instance = Repo(remoteSource, localSource)
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
            val cachedWeatherData = localSource.readCachedWeatherData()
            if (cachedWeatherData != null) {
                emit(ApiState.Successful(cachedWeatherData))
            }
            emit(ApiState.Loading)
            try {
                val result = remoteSource.getWeatherData(latitude, longitude)
                if (result.isSuccessful) {
                    emit(ApiState.Successful(result.body()))
                    localSource.cacheWeatherData(result.body())
                } else {
                    emit(ApiState.Failure(result.message()))
                }
            } catch (e: Exception) {
                emit(ApiState.Failure(e.message.toString()))
            }
        }
    }

    override suspend fun getWeatherDataAndAddToFavorite(
        latitude: Double,
        longitude: Double
    ): Long {
        return try {
            Log.i(TAG, "getWeatherDataAndAddToFavorite: on thread: ${Thread.currentThread().name}")
            val result = remoteSource.getWeatherData(latitude, longitude)
            if (result.isSuccessful && result.body() != null) {
                val fav = convertWeatherToFavorite(result.body()!!)
                localSource.addToFavorite(fav)
            } else {
                -1L
            }
        } catch (e: Exception) {
            Log.i(TAG, "getWeatherDataAndAddToFavorite exception: ${e.message}")
            -1L
        }
    }

    override suspend fun addToFavorite(location: FavoriteLocation) =
        localSource.addToFavorite(location)

    override suspend fun deleteFromFavorite(location: FavoriteLocation) =
        localSource.deleteFromFavorite(location)

    override fun getAllFavoriteLocations() = localSource.getAllFavoriteLocations()
}
