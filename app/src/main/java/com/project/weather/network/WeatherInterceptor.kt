package com.project.weather.network

import com.project.weather.constants.PrivateConstants
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale

class WeatherInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url =
            chain.request().url
                .newBuilder()
                .addQueryParameter("appid", PrivateConstants.API_KEY)
                .addQueryParameter("units", "metric")
                .addQueryParameter("lang", Locale.getDefault().language)
                .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()

        return chain.proceed(request)
    }
}