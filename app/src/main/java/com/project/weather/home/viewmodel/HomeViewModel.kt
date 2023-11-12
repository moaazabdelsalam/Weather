package com.project.weather.home.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.SharedViewModel
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepoInterface, private val sharedViewModel: SharedViewModel) :
    ViewModel() {
    private val TAG = "TAG HomeViewModel"

    private var _weatherDataApiStateFlow: MutableStateFlow<State<WeatherResponse?>> =
        MutableStateFlow(State.Loading)
    val weatherDataApiStateFlow: StateFlow<State<WeatherResponse?>>
        get() = _weatherDataApiStateFlow

    init {
        getHomeWeatherData()

        viewModelScope.launch(Dispatchers.IO) {
            repo.homeDataApiState.collectLatest {
                _weatherDataApiStateFlow.value = it
            }
        }
    }

    fun getHomeWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedViewModel.homeLocation.collect { homeLocation ->
                Log.i(TAG, "getHomeWeatherData of location: $homeLocation")
                if (homeLocation != null) {
                    repo.getWeatherDataOfHomeLocation(homeLocation.latitude, homeLocation.longitude)
                } else {
                    repo.getCachedWeatherDataAndUpdate()
                }
            }
        }
    }

    fun getHomeLocationSource() = sharedViewModel.homeLocationSource

    fun getTemperatureUnit() = sharedViewModel.temperatureUnitValue

    fun getSpeedUnit() = sharedViewModel.speedUnitValue
}