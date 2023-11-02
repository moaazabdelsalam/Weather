package com.project.weather.home.viewmodel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class HomeViewModel(private val repo: RepoInterface) : ViewModel() {
    private var _weatherData: MutableLiveData<WeatherResponse> = MutableLiveData()
    val weatherData: LiveData<WeatherResponse>
        get() = _weatherData

    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            val data = repo.getWeatherData(lat, lon)
            withContext(Dispatchers.Main) {
                _weatherData.postValue(data)
            }
        }
    }
}