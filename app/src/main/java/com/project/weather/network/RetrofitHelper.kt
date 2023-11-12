package com.project.weather.network

import com.project.weather.constants.Constants
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val logInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(WeatherInterceptor())
        //.addInterceptor(logInterceptor)
        .build()
    val weatherRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.OWM_BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
    val nominationRetrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.NOMINATION_BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object API {
    val weatherService: WeatherService by lazy {
        RetrofitHelper.weatherRetrofit.create(WeatherService::class.java)
    }
    val nominationService: NominationService by lazy {
        RetrofitHelper.nominationRetrofit.create(NominationService::class.java)
    }
}