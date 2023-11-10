package com.project.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import org.osmdroid.util.GeoPoint

class SharedViewModel : ViewModel() {
    private val TAG = "TAG SharedViewModel"

    private val _homeLocation: MutableStateFlow<GeoPoint?> =
        MutableStateFlow(null)
    val homeLocation: StateFlow<GeoPoint?>
        get() = _homeLocation
    private val _locationToBeAddedToFav: MutableStateFlow<GeoPoint?> =
        MutableStateFlow(null)
    val locationToBeAddedToFav: StateFlow<GeoPoint?>
        get() = _locationToBeAddedToFav

    fun setHomeLocation(latitude: Double, longitude: Double) {
        val location = GeoPoint(latitude, longitude)
        _homeLocation.value = location
        Log.i(TAG, "setHomeLocation: ${location.latitude}, ${location.longitude}")
    }

    fun setLocationToBeAddedToFavorite(geoPoint: GeoPoint) {
        _locationToBeAddedToFav.value = geoPoint
    }
}