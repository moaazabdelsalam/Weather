package com.project.weather.network

import android.util.Log
import com.project.weather.PrivateConstants
import okhttp3.Interceptor
import okhttp3.Response

class MyInterceptor : Interceptor {

    override fun intercept(chain: Interceptor.Chain): Response {
        val url =
            chain.request().url
                .newBuilder()
                .addQueryParameter("appid", PrivateConstants.API_KEY)
                .addQueryParameter("units", "metric")
                .build()
        val request = chain.request()
            .newBuilder()
            .url(url)
            .build()
        Log.i("TAG", "intercept: $request")

        return chain.proceed(request)
    }
}