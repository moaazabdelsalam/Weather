package com.project.weather.repo

import com.project.weather.model.ApiState
import kotlinx.coroutines.flow.Flow

interface RepoInterface {
    suspend fun getWeatherDataOfLocation(
        latitude: Double,
        longitude: Double
    ): Flow<ApiState>
}