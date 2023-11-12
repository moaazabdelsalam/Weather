package com.project.weather.network

import com.project.weather.model.ReverseNominationResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface NominationService {
    @GET("reverse")
    suspend fun getCityName(
        @Query("lat") latitude: Double,
        @Query("lon") longitude: Double,
        @Query("zoom") zoom: Int = 10,
        @Query("namedetails") nameDetails: Int = 1,
        @Query("format") format: String = "json"
    ): Response<ReverseNominationResponse>
//zoom=10&format=json&namedetails=1
}