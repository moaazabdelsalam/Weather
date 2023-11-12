package com.project.weather.repo

import kotlinx.coroutines.flow.StateFlow

interface PreferenceRepoInterface {
    val homeLocationSourceValue: StateFlow<String?>

    fun setHomeLocationSource(source: String)
}