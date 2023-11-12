package com.project.weather.setting.viewmodel

import androidx.lifecycle.ViewModel
import com.project.weather.SharedViewModel

class SettingViewModel(private val sharedViewModel: SharedViewModel) : ViewModel() {

    fun setHomeLocationSource(source: String) = sharedViewModel.setHomeLocationSource(source)

}