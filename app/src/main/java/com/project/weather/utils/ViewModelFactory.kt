package com.project.weather.utils

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.favorite.viewmodel.FavoriteViewModel
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.repo.RepoInterface

class ViewModelFactory(private val repo: RepoInterface) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repo) as T
        } else {
            throw NoSuchElementException("view model wasn't found")
        }
    }
}