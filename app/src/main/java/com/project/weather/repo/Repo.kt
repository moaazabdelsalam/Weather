package com.project.weather.repo

import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource

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

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): WeatherResponse = remoteSource.getWeatherData(latitude, longitude)

}