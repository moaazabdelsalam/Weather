package com.project.weather.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.model.State
import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.RepoInterface
import com.project.weather.utils.convertWeatherToFavorite
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MapViewModel(private val repo: RepoInterface) : ViewModel() {

    private var _weatherDataApiState: MutableSharedFlow<State<WeatherResponse?>?> =
        MutableSharedFlow()
    val weatherDataApiState: SharedFlow<State<WeatherResponse?>?>
        get() = _weatherDataApiState

    private var _cityNameApiState: MutableSharedFlow<State<ReverseNominationResponse?>?> =
        MutableSharedFlow()
    val cityNameApiState: SharedFlow<State<ReverseNominationResponse?>?>
        get() = _cityNameApiState

    init {
        viewModelScope.launch {
            _cityNameApiState.collect {
                if (it is State.Success) {
                    repo.homeDataApiState.collect { state ->
                        _weatherDataApiState.emit(state)
                    }
                }
            }
        }
    }

    fun getCityName(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getCityName(latitude, longitude).collectLatest {
                _cityNameApiState.emit(it)
            }
        }
    }

    fun getWeatherData(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherData(latitude, longitude).collectLatest {
                _weatherDataApiState.emit(it)
            }
        }
    }

    suspend fun addToFavorite(weatherResponse: WeatherResponse, cityName: String): Long {
        return viewModelScope.async(Dispatchers.IO) {
            repo.addToFavorite(convertWeatherToFavorite(weatherResponse, cityName))
        }.await()
    }

    fun setHomeLocation(latitude: Double, longitude: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.setHomeLocation(latitude, longitude)
        }
    }

    fun setHomeLocationSource(source: String) {

    }
}