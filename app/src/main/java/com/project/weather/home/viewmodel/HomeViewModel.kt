package com.project.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.model.ApiState
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepoInterface) : ViewModel() {
    private var _weatherDataStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherDataStateFlow: StateFlow<ApiState>
        get() = _weatherDataStateFlow

    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherDataOfLocation(lat, lon).collect {
                _weatherDataStateFlow.value = it
            }
        }
    }
}