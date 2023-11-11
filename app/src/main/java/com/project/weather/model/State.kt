package com.project.weather.model

sealed class State {
    object Loading : State()
    data class Successful(val data: WeatherResponse?) : State()
    data class Failure(val error: String) : State()
}
