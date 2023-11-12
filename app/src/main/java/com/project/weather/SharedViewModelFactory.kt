package com.project.weather

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.repo.PreferenceRepo

class SharedViewModelFactory(private val preferenceRepo: PreferenceRepo) : ViewModelProvider.Factory  {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(SharedViewModel::class.java)) {
            SharedViewModel(preferenceRepo) as T
        } else {
            throw Exception("can't create shared view model")
        }
    }
}