package com.project.weather.repo

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class FakePrefRepo: PreferenceRepoInterface {
    override val homeLocationSourceValue: StateFlow<String?>
        get() = MutableStateFlow("GPS")
    override val temperatureUnitValue: StateFlow<String?>
        get() = MutableStateFlow("C")
    override val speedUnitValue: StateFlow<String?>
        get() = MutableStateFlow("m\\s")

    override fun setHomeLocationSource(source: String) {

    }

    override fun setLanguage(language: String) {

    }

    override fun setTemperatureUnit(unit: String) {

    }

    override fun setSpeedUnit(unit: String) {

    }
}