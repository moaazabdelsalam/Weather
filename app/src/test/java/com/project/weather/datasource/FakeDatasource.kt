package com.project.weather.datasource


import com.project.weather.local.LocalSource
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import kotlinx.coroutines.flow.flow
import retrofit2.Response

class FakeDatasource(var favWeather:MutableList<FavoriteLocation>? = mutableListOf()) : LocalSource,
    RemoteSource {

    override suspend fun addToFavorite(location: FavoriteLocation): Long {
        favWeather?.add(location)
        return if(favWeather?.contains(location) == true){
            1L
        } else{
            0L
        }
    }

    override suspend fun deleteFromFavorite(location: FavoriteLocation): Int {
        favWeather?.remove(location)
        return if(favWeather?.contains(location) == true){
            0
        } else{
            1
        }
    }

    override fun getAllFavoriteLocations()= flow{
        val listFav:List<FavoriteLocation> = favWeather!!
        emit(listFav)
    }

    override suspend fun cacheWeatherData(weatherData: WeatherResponse?) {

    }

    override suspend fun readCachedWeatherData(): WeatherResponse? {

        return null
    }

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse> {
        return Response.success(null)
    }


}