package com.project.weather.setting.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.SharedViewModel
import com.project.weather.repo.PreferenceRepo

class SettingViewModelFactory(val sharedViewModel: SharedViewModel): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SettingViewModel::class.java)) {
            SettingViewModel(sharedViewModel) as T
        } else {
            throw Exception("can't create setting view model")
        }
    }
}