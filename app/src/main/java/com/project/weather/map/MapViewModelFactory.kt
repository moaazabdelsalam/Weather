package com.project.weather.map

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.repo.RepoInterface
import com.project.weather.utils.ViewModelFactory

class MapViewModelFactory(private val repo: RepoInterface): ViewModelProvider.Factory {

    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if(modelClass.isAssignableFrom(MapViewModel::class.java)) {
            MapViewModel(repo) as T
        } else {
            throw Exception("cant create MapViewModel")
        }
    }
}