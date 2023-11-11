package com.project.weather.repo

import android.util.Log
import com.project.weather.local.LocalSource
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import retrofit2.Response

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

    override suspend fun getWeatherDataOfHomeLocation(
        latitude: Double,
        longitude: Double
    ): Flow<State> {
        return flow<State> {
            val cachedWeatherData = localSource.readCachedWeatherData()
            if (cachedWeatherData != null) {
                emit(State.Successful(cachedWeatherData))
            }
            emit(State.Loading)
            try {
                val response = remoteSource.getWeatherData(latitude, longitude)
                if (response.isSuccessful) {
                    emit(State.Successful(response.body()))
                    localSource.cacheWeatherData(response.body())
                } else {
                    emit(State.Failure(response.message()))
                }
            } catch (e: Exception) {
                Log.i(TAG, "getWeatherDataOfHomeLocation exception: $e")
                emit(State.Failure(e.message.toString()))
            }
        }
    }

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Flow<State> {
        return wrapWithFlow(remoteSource.getWeatherData(latitude, longitude))
    }

    override suspend fun addToFavorite(location: FavoriteLocation) =
        localSource.addToFavorite(location)

    override suspend fun deleteFromFavorite(location: FavoriteLocation) =
        localSource.deleteFromFavorite(location)

    override fun getAllFavoriteLocations() = localSource.getAllFavoriteLocations()

    private fun wrapWithFlow(response: Response<WeatherResponse>): Flow<State> {
        return flow {
            emit(State.Loading)
            try {
                if (response.isSuccessful) {
                    emit(State.Successful(response.body()))
                    localSource.cacheWeatherData(response.body())
                } else {
                    emit(State.Failure(response.message()))
                }
            } catch (e: Exception) {
                Log.i(TAG, "wrapWithFlow exception: $e")
                emit(State.Failure(e.message.toString()))
            }
        }
    }
}
