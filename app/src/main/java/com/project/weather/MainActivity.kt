package com.project.weather

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.network.WeatherClient
import com.project.weather.repo.Repo

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val viewModelFactory = ViewModelFactory(
            Repo.getInstance(
                WeatherClient
            )
        )
        val homeViewModel = ViewModelProvider(this, viewModelFactory)[HomeViewModel::class.java]
        homeViewModel.getWeatherData(33.44, -94.04)
        homeViewModel.weatherData.observe(this) {
            Log.i("TAG", "weather data: ${it.current}")
        }
    }
}