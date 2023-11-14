package com.project.weather.datasource

import com.project.weather.model.Address
import com.project.weather.model.Current
import com.project.weather.model.Daily
import com.project.weather.model.FeelsLike
import com.project.weather.model.Hourly
import com.project.weather.model.NameDetails
import com.project.weather.model.ReverseNominationResponse
import com.project.weather.model.Temperature
import com.project.weather.model.Weather
import com.project.weather.model.WeatherResponse
import com.project.weather.network.RemoteSource
import retrofit2.Response

class FakeRemoteDatasource(
    private val weatherResponse: WeatherResponse = WeatherResponse(
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
    ),
    private val reverseNominationResponse: ReverseNominationResponse = ReverseNominationResponse(
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

) : RemoteSource {
    override suspend fun getWeatherData(
        latitude: Double,
        longitude: Double
    ): Response<WeatherResponse> {
        return Response.success(weatherResponse)
    }

    override suspend fun getCityName(
        latitude: Double,
        longitude: Double
    ): Response<ReverseNominationResponse> {
        return Response.success(reverseNominationResponse)
    }
}