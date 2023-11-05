package com.project.weather.model

sealed class ApiState {
    object Loading : ApiState()
    data class Successful(val data: WeatherResponse?) : ApiState()
    data class Failure(val error: String) : ApiState()
}
