package com.project.weather.favorite.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.SharedViewModel
import com.project.weather.repo.RepoInterface

class FavoriteViewModelFactory(private val repo: RepoInterface, private val sharedViewModel: SharedViewModel) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(FavoriteViewModel::class.java)) {
            FavoriteViewModel(repo, sharedViewModel) as T
        } else
            throw Exception("cant create favorite view model")
    }
}