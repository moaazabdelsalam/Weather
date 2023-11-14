package com.project.weather.datasource


import com.project.weather.local.LocalSource
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.flow.flow

class FakeLocalDatasource(private var favWeather: MutableList<FavoriteLocation>? = mutableListOf()) :
    LocalSource {

    override suspend fun addToFavorite(location: FavoriteLocation): Long {
        favWeather?.add(location)
        return if (favWeather?.contains(location) == true) {
            1L
        } else {
            0L
        }
    }

    override suspend fun deleteFromFavorite(location: FavoriteLocation): Int {
        favWeather?.remove(location)
        return if (favWeather?.contains(location) == true) {
            0
        } else {
            1
        }
    }

    override fun getAllFavoriteLocations() = flow {
        val listFav: List<FavoriteLocation> = favWeather!!
        emit(listFav)
    }

    override suspend fun updateAlert(location: FavoriteLocation) {
        favWeather?.let {
            if (favWeather?.contains(location) == true) {
                val index = favWeather!!.indexOf(location)
                favWeather!![index].isScheduled = !favWeather!![index].isScheduled
            }
        }
    }

    override suspend fun cacheWeatherData(weatherData: WeatherResponse?) {

    }

    override suspend fun readCachedWeatherData(): State<WeatherResponse?> {

        return State.Loading
    }
}