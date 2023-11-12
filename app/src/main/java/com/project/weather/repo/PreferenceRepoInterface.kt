package com.project.weather.repo

import kotlinx.coroutines.flow.StateFlow

interface PreferenceRepoInterface {
    val homeLocationSourceValue: StateFlow<String?>
    val temperatureUnitValue: StateFlow<String?>
    val speedUnitValue: StateFlow<String?>

    fun setHomeLocationSource(source: String)

    fun setLanguage(language: String)

    fun setTemperatureUnit(unit: String)

    fun setSpeedUnit(unit: String)
}