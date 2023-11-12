package com.project.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.project.weather.repo.PreferenceRepo
import com.project.weather.repo.PreferenceRepoInterface
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import org.osmdroid.util.GeoPoint

class SharedViewModel(private val preferenceRepo: PreferenceRepoInterface) : ViewModel() {
    private val TAG = "TAG SharedViewModel"

    private val _homeLocationSource: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val homeLocationSource: StateFlow<String?>
        get() = _homeLocationSource

    private val _homeLocation: MutableStateFlow<GeoPoint?> =
        MutableStateFlow(null)
    val homeLocation: StateFlow<GeoPoint?>
        get() = _homeLocation

    init {
        viewModelScope.launch(Dispatchers.IO) {
            preferenceRepo.homeLocationSourceValue.collectLatest {
                Log.i(TAG, "getHomeLocationSource: $it")
                _homeLocationSource.value = it
            }
        }
    }

    fun setHomeLocationSource(source: String) {
        Log.i(TAG, "setHomeLocationSource to: $source")
        preferenceRepo.setHomeLocationSource(source)
    }

    fun setHomeLocation(geoPoint: GeoPoint?) {
        _homeLocation.value = geoPoint
        Log.i(TAG, "setHomeLocation: ${geoPoint?.latitude}, ${geoPoint?.longitude}")
    }
}