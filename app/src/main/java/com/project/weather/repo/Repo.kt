package com.project.weather.repo

import android.util.Log
import com.project.weather.local.LocalSource
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.State
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.launch
import retrofit2.Response

class Repo private constructor(
    private val remoteSource: RemoteSource,
    private val localSource: LocalSource
) : RepoInterface {
    val TAG = "TAG Repo"

    private var _homeDataApiState: MutableSharedFlow<State<WeatherResponse?>> =
        MutableSharedFlow()
    override val homeDataApiState: SharedFlow<State<WeatherResponse?>>
        get() = _homeDataApiState

    companion object {
        @Volatile
        private var _instance: Repo? = null
        fun getInstance(remoteSource: RemoteSource, localSource: LocalSource): Repo {
            return _instance ?: synchronized(this) {
                val instance = Repo(remoteSource, localSource)
                _instance = instance
                instance
            }
        }
    }

    init {
        GlobalScope.launch {
            _homeDataApiState.collect { state ->
                if (state is State.Success) {
                    localSource.cacheWeatherData(state.data)
                }
            }
        }
    }

    override suspend fun getWeatherDataOfHomeLocation(
        latitude: Double,
        longitude: Double
    ) {
        val cachedWeatherData = localSource.readCachedWeatherData()
        if (cachedWeatherData != null) {
            _homeDataApiState.emit(State.Success(cachedWeatherData))
        }
        _homeDataApiState.emit(State.Loading)
        try {
            val response = remoteSource.getWeatherData(latitude, longitude)
            if (response.isSuccessful) {
                response.body()?.let { setCityName(it) }
                //_homeDataApiState.emit(State.Success(response.body()))
                //localSource.cacheWeatherData(response.body())
            } else {
                _homeDataApiState.emit(State.Failure(response.message()))
            }
        } catch (e: Exception) {
            Log.i(TAG, "getWeatherDataOfHomeLocation exception: $e")
            _homeDataApiState.emit(State.Failure(e.message.toString()))

        }
    }

    override suspend fun getCachedWeatherDataAndUpdate() {
        val cachedWeatherData = localSource.readCachedWeatherData()
        if (cachedWeatherData != null) {
            _homeDataApiState.emit(State.Success(cachedWeatherData))
            _homeDataApiState.emit(State.Loading)
            try {
                val response =
                    remoteSource.getWeatherData(cachedWeatherData.lat, cachedWeatherData.lon)
                if (response.isSuccessful) {
                    response.body()?.let { setCityName(it) }
                    //_homeDataApiState.emit(State.Success(response.body()))
                    //localSource.cacheWeatherData(response.body())
                } else {
                    _homeDataApiState.emit(State.Failure(response.message()))
                }
            } catch (e: Exception) {
                Log.i(TAG, "getCachedWeatherDataAndUpdate exception: $e")
                _homeDataApiState.emit(State.Failure(e.message.toString()))
            }
        }
    }

    override suspend fun setHomeLocation(latitude: Double, longitude: Double) {
        try {
            val response = remoteSource.getWeatherData(latitude, longitude)
            if (response.isSuccessful) {
                response.body()?.let { setCityName(it) }
                //_homeDataApiState.emit(State.Success(response.body()))
                //localSource.cacheWeatherData(response.body())
            } else {
                _homeDataApiState.emit(State.Failure(response.message()))
            }
        } catch (e: Exception) {
            Log.i(TAG, "getWeatherDataOfHomeLocation exception: $e")
            _homeDataApiState.emit(State.Failure(e.message.toString()))

        }
    }

    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Flow<State<WeatherResponse?>> {
        return wrapWithFlow(remoteSource.getWeatherData(latitude, longitude))
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Flow<State<ReverseNominationResponse>> {
        return flow {
            emit(State.Loading)
            try {
                val result = remoteSource.getCityName(latitude, longitude)
                if (result.isSuccessful) {
                    emit(State.Success(result.body()))
                } else {
                    Log.i(TAG, "getCityName fail: ${result.message()}")
                    emit(State.Failure(result.message()))
                }
            } catch (e: Exception) {
                Log.i(TAG, "getCityName exception: ${e.message}")
                emit(State.Failure(e.message ?: "null"))
            }
        }
    }

    private suspend fun setCityName(weatherResponse: WeatherResponse) {
        getCityName(weatherResponse.lat, weatherResponse.lon).collectLatest {
            if (it is State.Success && it.data != null) {
                weatherResponse.nameAr = it.data.namedetails.nameAr
                weatherResponse.nameEn = it.data.namedetails.nameEn
                _homeDataApiState.emit(State.Success(weatherResponse))
            }
        }
    }

    override suspend fun addToFavorite(location: FavoriteLocation) =
        localSource.addToFavorite(location)

    override suspend fun deleteFromFavorite(location: FavoriteLocation) =
        localSource.deleteFromFavorite(location)

    override fun getAllFavoriteLocations() = localSource.getAllFavoriteLocations()

    private fun wrapWithFlow(response: Response<WeatherResponse>): Flow<State<WeatherResponse?>> {
        return flow {
            emit(State.Loading)
            try {
                if (response.isSuccessful) {
                    emit(State.Success(response.body()))
                } else {
                    emit(State.Failure(response.message()))
                }
            } catch (e: Exception) {
                Log.i(TAG, "wrapWithFlow exception: $e")
                emit(State.Failure(e.message.toString()))
            }
        }
    }
}
