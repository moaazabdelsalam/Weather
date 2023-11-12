package com.project.weather

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
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
    private val _temperatureUnitValue: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val temperatureUnitValue: StateFlow<String?>
        get() = _temperatureUnitValue
    private val _speedUnitValue: MutableStateFlow<String?> =
        MutableStateFlow(null)
    val speedUnitValue: StateFlow<String?>
        get() = _speedUnitValue
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

        viewModelScope.launch(Dispatchers.IO) {
            preferenceRepo.temperatureUnitValue.collectLatest {
                _temperatureUnitValue.value = it
            }
        }

        viewModelScope.launch(Dispatchers.IO) {
            preferenceRepo.speedUnitValue.collectLatest {
                _speedUnitValue.value = it
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

    fun setLanguage(language: String) {
        preferenceRepo.setLanguage(language)
    }

    fun setTemperatureUnit(unit: String) {
        preferenceRepo.setTemperatureUnit(unit)
    }

    fun setSpeedUnit(unit: String) {
        preferenceRepo.setSpeedUnit(unit)
    }
}