package com.project.weather.home.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.project.weather.SharedViewModel
import com.project.weather.repo.RepoInterface

class HomeViewModelFactory(private val repo: RepoInterface, private val sharedViewModel: SharedViewModel) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return if (modelClass.isAssignableFrom(HomeViewModel::class.java)) {
            HomeViewModel(repo, sharedViewModel) as T
        } else {
            throw NoSuchElementException("view model wasn't found")
        }
    }
}