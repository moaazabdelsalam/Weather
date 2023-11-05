package com.project.weather.repo

import com.project.weather.model.ApiState
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow

class Repo private constructor(private val remoteSource: RemoteSource) : RepoInterface {

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
            emit(ApiState.Loading)
            try {
                val result = remoteSource.getWeatherData(latitude, longitude)
                if (result.isSuccessful) {
                    emit(ApiState.Successful(result.body()))
                } else {
                    emit(ApiState.Failure(result.message()))
                }
            } catch (e: Exception) {
                emit(ApiState.Failure(e.message.toString()))
            }
        }
    }

}