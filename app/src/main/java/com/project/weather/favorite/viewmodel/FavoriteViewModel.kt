package com.project.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.SharedViewModel
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class FavoriteViewModel(
    private val repo: RepoInterface,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val TAG = "TAG FavoriteViewModel"

    private var _selectedItem: MutableSharedFlow<State<WeatherResponse?>> =
        MutableSharedFlow()
    val selectedItem: SharedFlow<State<WeatherResponse?>>
        get() = _selectedItem

    private var _favoriteList: MutableStateFlow<State<List<FavoriteLocation>>> =
        MutableStateFlow(State.Loading)
    val favoriteList: StateFlow<State<List<FavoriteLocation>>>
        get() = _favoriteList

    init {
        getAllFavorite()
    }

    fun getAllFavorite() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations().collectLatest { list ->
                withContext(Dispatchers.Main) {
                    if (list.isEmpty())
                        _favoriteList.value = State.Failure("No Data Found...")
                    else
                        _favoriteList.value = State.Success(list)
                }
            }
        }
    }

    fun deleteLocationFromFavorite(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromFavorite(location)
        }
    }

    fun addAlert(location: FavoriteLocation, time: String) {
        location.isScheduled = true
        location.timeString = time
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAlert(location)
        }
    }

    fun dismissAlert(location: FavoriteLocation) {
        location.isScheduled = false
        location.timeString = ""
        viewModelScope.launch(Dispatchers.IO) {
            repo.updateAlert(location)
        }
    }

    fun setSelectedItem(fav: FavoriteLocation) {
        viewModelScope.launch {
            repo.getWeatherData(fav.lat, fav.lon).collectLatest {
                if (it is State.Success) {
                    it.data?.nameEn = fav.cityName
                    _selectedItem.emit(State.Success(it.data))
                } else
                    _selectedItem.emit(it)
            }
        }
    }
}