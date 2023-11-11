package com.project.weather.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.RepoInterface
import com.project.weather.utils.convertWeatherToFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel(private val repo: RepoInterface) : ViewModel() {

    private var _weatherDataStateFlow: MutableStateFlow<State?> =
        MutableStateFlow(null)
    val weatherDataStateFlow: StateFlow<State?>
        get() = _weatherDataStateFlow

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherData(latitude, longitude).collectLatest {
                _weatherDataStateFlow.value = it
            }
        }
    }

    suspend fun addToFavorite(weatherResponse: WeatherResponse): Long {
        return viewModelScope.async(Dispatchers.IO) {
            repo.addToFavorite(convertWeatherToFavorite(weatherResponse))
        }.await()
    }
}