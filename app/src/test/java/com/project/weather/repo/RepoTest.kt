package com.project.weather.repo

import com.project.weather.datasource.FakeLocalDatasource
import com.project.weather.datasource.FakeRemoteDatasource
import com.project.weather.model.Address
import com.project.weather.model.Current
import com.project.weather.model.Daily
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.FeelsLike
import com.project.weather.model.Hourly
import com.project.weather.model.NameDetails
import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.State
import com.project.weather.model.Temperature
import com.project.weather.model.Weather
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.toList
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class RepoTest {
    private val databaseWeather1 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather2 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather3 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather4 =
        FavoriteLocation(12.3, 32.1, "El-Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)
    private val databaseWeather5 =
        FavoriteLocation(12.3, 32.1, "Mahalla", "Clear", "Clear sky", "src", 2.2, 32.2)

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

    val reverseNominationResponse: ReverseNominationResponse = ReverseNominationResponse(
        "El-Mahalla",
        Address(
            "Gharbia",
            "Egypt",
            "Egypt",
            "7422"
        ),
        NameDetails(
            "Mahalla",
            "El-Mahalla",
            "المحله الكبرى"
        )
    )

    private val fakeLocalData = mutableListOf(
        databaseWeather1,
        databaseWeather2,
        databaseWeather3,
        databaseWeather4,
        databaseWeather5
    )

    private lateinit var fakeLocalDatasource: FakeLocalDatasource
    private lateinit var fakeRemoteDatasource: FakeRemoteDatasource
    private lateinit var repo: Repo

    @Before
    fun setup() {
        fakeLocalDatasource = FakeLocalDatasource(fakeLocalData)
        fakeRemoteDatasource = FakeRemoteDatasource(weatherResponse, reverseNominationResponse)
        repo = Repo.getInstance(fakeRemoteDatasource, fakeLocalDatasource)
    }

    @Test
    fun addWeather_Not_Exist() = runBlocking {
        val fav = FavoriteLocation(74.22, 22.74, "MASR", "Clear", "DEsc", "10d", 2.2, 7.4)
        val result = repo.addToFavorite(fav)
        assertEquals(result, 1L)
    }

    @Test
    fun addWeather_Exist() = runBlocking {
        val result = repo.addToFavorite(databaseWeather1)
        assertEquals(result, 1L)
    }

    @Test
    fun delete_Exist() = runBlocking {
        val result = repo.deleteFromFavorite(databaseWeather4)
        assertEquals(result, 1)
    }

    @Test
    fun delete_Not_Exist() = runBlocking {
        val fav = FavoriteLocation(74.220 , 22.74 ,"MASR" , "Clear", "DEsc","10d", 2.2, 7.4)
        val result = repo.deleteFromFavorite(fav)
        assertEquals(result, 1)
    }

    @Test
    fun getAllFavWeather() = runBlocking {
        val flow = flowOf(fakeLocalData)
        val result = flow.toList()
        assertEquals(listOf(fakeLocalData), result)
    }

    @Test
    fun updateAlert_Enabled_Success() = runBlocking {
        val fav = fakeLocalData[0]
        repo.updateAlert(fav)
        assertEquals(true, fav.isScheduled)
    }

    @Test
    fun getHomeData_Success() = runBlocking {
        var data: WeatherResponse? = null
        repo.getWeatherDataOfHomeLocation(7.4, 2.2)
        val job = launch {
            repo.homeDataApiState.collectLatest {
                if (it is State.Success){
                    data = it.data
                }
            }
        }
        assertEquals(false , data != null)
        job.cancel()
    }
}