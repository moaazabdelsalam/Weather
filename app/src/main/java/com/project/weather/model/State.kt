package com.project.weather.model

sealed class State<out T> {
    object Loading : State<Nothing>()
    data class Success<T>(val data: T?) : State<T>()
    data class Failure(val error: String) : State<Nothing>()
}
