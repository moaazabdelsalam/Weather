package com.project.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class SharedViewModel : ViewModel() {
    private val TAG = "TAG SharedViewModel"

    private val _homeLocation: MutableStateFlow<Pair<Double, Double>> =
        MutableStateFlow(Pair(0.0, 0.0))
    val homeLocation: StateFlow<Pair<Double, Double>>
        get() = _homeLocation

    fun setHomeLocation(latitude: Double, longitude: Double) {
        val location = Pair(latitude, longitude)
        _homeLocation.value = location
        Log.i(TAG, "setHomeLocation: ${location.first}, ${location.second}")
    }
}