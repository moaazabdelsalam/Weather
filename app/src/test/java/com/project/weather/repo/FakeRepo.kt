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
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.flow

class FakeRepo(private val localSource: FakeLocalDatasource, private val remoteSource: FakeRemoteDatasource): RepoInterface {

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

    private val localWeather = mutableListOf<FavoriteLocation>(
        databaseWeather1,
        databaseWeather2,
        databaseWeather3,
        databaseWeather4,
        databaseWeather5
    )

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

    private var _homeDataApiState: MutableSharedFlow<State<WeatherResponse?>> =
        MutableSharedFlow()
    override val homeDataApiState: SharedFlow<State<WeatherResponse?>>
        get() = _homeDataApiState

    override suspend fun getWeatherDataOfHomeLocation(latitude: Double, longitude: Double) {
        _homeDataApiState.emit(State.Success(weatherResponse))
    }

    override suspend fun getCachedWeatherDataAndUpdate() {
        TODO("Not yet implemented")
    }

    override suspend fun setHomeLocation(latitude: Double, longitude: Double) {
        TODO("Not yet implemented")
    }

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Flow<State<WeatherResponse?>> {
        TODO("Not yet implemented")
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Flow<State<ReverseNominationResponse>> {
        val response = remoteSource.getCityName(latitude, longitude)
        return flow {
            if (response.isSuccessful){
                emit(State.Success(response.body()))
            } else {
                emit(State.Failure(response.message()))
            }
        }
    }

    override suspend fun getCachedWeatherData(): State<WeatherResponse?> {
        return State.Loading
    }

    override suspend fun addToFavorite(location: FavoriteLocation): Long {
        return localSource.addToFavorite(location)
    }

    override suspend fun deleteFromFavorite(location: FavoriteLocation): Int {
        return localSource.deleteFromFavorite(location)
    }

    override fun getAllFavoriteLocations(): Flow<List<FavoriteLocation>> {
        return localSource.getAllFavoriteLocations()
    }

    override suspend fun updateAlert(location: FavoriteLocation) {
        return localSource.updateAlert(location)
    }
}