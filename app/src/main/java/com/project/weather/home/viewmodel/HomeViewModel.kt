package com.project.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.SharedViewModel
import com.project.weather.model.ApiState
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class HomeViewModel(private val repo: RepoInterface, private val sharedViewModel: SharedViewModel) :
    ViewModel() {
    private var _weatherDataStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherDataStateFlow: StateFlow<ApiState>
        get() = _weatherDataStateFlow

    init {
        getHomeWeatherData()
    }

    fun getHomeWeatherData() {
        viewModelScope.launch(Dispatchers.IO) {
            sharedViewModel.homeLocation.collectLatest { homeLocation ->
                homeLocation?.let {
                    repo.getWeatherDataOfLocation(homeLocation.latitude, homeLocation.longitude)
                        .collectLatest {
                            _weatherDataStateFlow.value = it
                        }
                }
            }
        }
    }
}