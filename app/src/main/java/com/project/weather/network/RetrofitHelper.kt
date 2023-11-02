package com.project.weather.network

import com.project.weather.Constants
import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitHelper {
    private val client: OkHttpClient = OkHttpClient.Builder()
        .addInterceptor(MyInterceptor())
        .build()

    val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(Constants.BASE_URL)
        .client(client)
        .addConverterFactory(GsonConverterFactory.create())
        .build()
}

object API {
    val retrofitService: WeatherService by lazy {
        RetrofitHelper.retrofit.create(WeatherService::class.java)
    }
}