package com.project.weather.homeviewmodel

import androidx.test.ext.junit.runners.AndroidJUnit4
import com.project.weather.SharedViewModel
import com.project.weather.datasource.FakeLocalDatasource
import com.project.weather.datasource.FakeRemoteDatasource
import com.project.weather.home.viewmodel.HomeViewModel
import com.project.weather.model.Current
import com.project.weather.model.Daily
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.FeelsLike
import com.project.weather.model.Hourly
import com.project.weather.model.State
import com.project.weather.model.Temperature
import com.project.weather.model.Weather
import com.project.weather.model.WeatherResponse
import com.project.weather.repo.FakePrefRepo
import com.project.weather.repo.FakeRepo
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class HomeViewModelTest {
    private lateinit var repo: FakeRepo
    private lateinit var homeViewModel: HomeViewModel
    val weatherResponse = WeatherResponse(
        7.4,
        2.2,
        "El-Mahalla",
        22L,
        Current(
            22L,
            22L,
            22L,
            7.4,
            7.4,
            22L,
            22L,
            7.4,
            15.5,
            22L,
            22L,
            7.4,
            22L,
            7.4,
            listOf(
                Weather(
                    1L, "Clear", "Clear Sky", "01d"
                )
            )
        ),
        listOf(
            Hourly(
                22L,
                2.2,
                2.2,
                74,
                74,
                2.2,
                2.2,
                74,
                74,
                2.2,
                22L,
                7.4,
                listOf(Weather(1L, "Clear", "Clear Sky", "01d")),
                7.4,
            )
        ), listOf(
            Daily(
                1699779600,
                1699762756,
                1699801186,
                1699758840,
                1699798680,
                0.97,
                Temperature(15.5, 15.5, 15.5, 15.5, 15.5, 15.5),

                FeelsLike(15.5, 15.5, 15.5, 15.5),
                155,
                155, 15.5, 15.5, 155, 15.5,
                listOf(Weather(1L, "Clear", "Clear Sky", "01d")),
                155, 15.15, 15.5
            )
        ),
        null,
        "المحله الكبرى", "El-Mahalla"
    )

    @Before
    fun setup() {
        repo =
            FakeRepo(FakeLocalDatasource(mutableListOf<FavoriteLocation>()), FakeRemoteDatasource())
        homeViewModel = HomeViewModel(
            repo,
            SharedViewModel(FakePrefRepo())
        )
    }

    @Test
    fun getCurrentWeather() = runBlocking {
        var result: WeatherResponse? = null
        val job = launch {
            repo.getWeatherDataOfHomeLocation(7.4, 2.2)
            homeViewModel.weatherDataApiStateFlow.collect {
                if (it is State.Success)
                    result = it.data
                assertEquals(weatherResponse, result)
            }
        }
        job.cancel()
    }
}