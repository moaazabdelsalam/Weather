package com.project.weather.favorite.viewmodel

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.SharedViewModel
import com.project.weather.model.FavoriteLocation
import com.project.weather.repo.RepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class FavoriteViewModel(
    private val repo: RepoInterface,
    private val sharedViewModel: SharedViewModel
) : ViewModel() {
    private val TAG = "TAG FavoriteViewModel"

    private var _addToFavoriteState: MutableStateFlow<Long?> =
        MutableStateFlow(null)
    val addToFavoriteState: StateFlow<Long?>
        get() = _addToFavoriteState

    private var _favoriteList: MutableStateFlow<List<FavoriteLocation>> = MutableStateFlow(listOf())
    val favoriteList: StateFlow<List<FavoriteLocation>>
        get() = _favoriteList

    init {
        getFavoriteLocations()

        viewModelScope.launch(Dispatchers.IO) {
            sharedViewModel.locationToBeAddedToFav.collectLatest {
                it?.let {
                    val result = repo.getWeatherDataAndAddToFavorite(it.latitude, it.longitude)
                    Log.i(TAG, "adding to favorite location: $result")
                    _addToFavoriteState.value = result
                }
            }
        }
    }

    private fun getFavoriteLocations() {
        viewModelScope.launch(Dispatchers.IO) {
            repo.getAllFavoriteLocations().collectLatest {
                _favoriteList.value = it
            }
        }
    }

    fun deleteLocationFromFavorite(location: FavoriteLocation) {
        viewModelScope.launch(Dispatchers.IO) {
            repo.deleteFromFavorite(location)
        }
    }
}