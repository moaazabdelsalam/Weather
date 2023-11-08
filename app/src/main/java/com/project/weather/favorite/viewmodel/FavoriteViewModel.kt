package com.project.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.model.ApiState
import com.project.weather.model.FavoriteLocation
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(private val repo: RepoInterface) : ViewModel() {
    private var _weatherDataStateFlow: MutableStateFlow<ApiState> =
        MutableStateFlow(ApiState.Loading)
    val weatherDataStateFlow: StateFlow<ApiState>
        get() = _weatherDataStateFlow

    private var _favoriteList: MutableStateFlow<List<FavoriteLocation>> = MutableStateFlow(listOf())
    val favoriteList: StateFlow<List<FavoriteLocation>>
        get() = _favoriteList

    init {
        getFavoriteLocations()
    }

    private fun getFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations().collectLatest {
                _favoriteList.value = it
            }
        }
    }

    fun getWeatherData(lat: Double, lon: Double) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getWeatherDataOfLocation(lat, lon).collect {
                _weatherDataStateFlow.value = it
            }
        }
    }

    fun addLocationToFavorite(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.addToFavorite(location)
        }
    }

    fun deleteLocationFromFavorite(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromFavorite(location)
        }
    }
}