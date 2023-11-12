package com.project.weather.utils

import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import com.project.weather.constants.Constants
import com.project.weather.model.FavoriteLocation
import com.project.weather.model.WeatherResponse
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun <T> Fragment.collectLatestFlowOnLifecycle(
    flow: Flow<T>,
    collect: suspend (T) -> Unit
) {
    lifecycleScope.launch {
        repeatOnLifecycle(Lifecycle.State.STARTED) {
            flow.collectLatest(collect)
        }
    }
}

fun getDateAndTime(s: Long): Map<String, String> {
    val sdf = SimpleDateFormat("EEE MMM dd yyyy hh:mm aa", Locale.ENGLISH)
    val netDate = Date(s * 1000)
    val result = (sdf.format(netDate)).split(" ")
    return mapOf(
        Constants.DAY_OF_WEEK_KEY to result[0],
        Constants.MONTH_KEY to result[1],
        Constants.DAY_OF_MONTH_KEY to result[2],
        Constants.YEAR_KEY to result[3],
        Constants.TIME_KEY to result[4],
        Constants.AM_PM_KEY to result[5]
    )
}

fun convertWeatherToFavorite(weatherResponse: WeatherResponse, cityName: String): FavoriteLocation {
    return FavoriteLocation(
        weatherResponse.lat,
        weatherResponse.lon,
        cityName,
        weatherResponse.current.weather[0].main,
        weatherResponse.current.weather[0].description,
        weatherResponse.current.weather[0].icon,
        weatherResponse.daily[0].temp.min,
        weatherResponse.daily[0].temp.max
    )
}

/*fun getIconDrawableId(icon: String) =
    when (icon) {
        "01d" -> R.drawable.clear_d
        "01n" -> R.drawable.clear_n
        "02d" -> R.drawable.few_clouds_d
        "02n" -> R.drawable.few_clouds_n
        "03d" -> R.drawable.scattered_clouds_d
        "03n" -> R.drawable.scattered_clouds_n
        "04d" -> R.drawable.broken_clouds_d
        "04n" -> R.drawable.broken_clouds_n
        "09d" -> R.drawable.shower_rain_d
        "09n" -> R.drawable.shower_rain_n
        "10d" -> R.drawable.rain_d
        "10n" -> R.drawable.rain_n
        "11d" -> R.drawable.thunder_d
        "11n" -> R.drawable.thunder_n
        "13d" -> R.drawable.snow
        "13n" -> R.drawable.snow
        "50d" -> R.drawable.mist
        "50n" -> R.drawable.mist
        else -> R.drawable.weather_icon_placeholder
    }*/

fun getIconLink(icon: String) =
    when (icon) {
        "01d" -> "https://drive.google.com/uc?export=download&id=1DaphQxaWN9XDWhqmYGzqZKcuH0Yx_Qzj"
        "01n" -> "https://drive.google.com/uc?export=download&id=1634mPZmwkrV_Q1prFDhqbTCoswdRMwIQ"
        "02d" -> "https://drive.google.com/uc?export=download&id=1i8N-nEaiT3nkG2pVYyybnJAEQZdxfoNQ"
        "02n" -> "https://drive.google.com/uc?export=download&id=1FHTJuhFukS6rI1lHfVTzI04sYJqThM4b"
        "03d" -> "https://drive.google.com/uc?export=download&id=1Na17XLjsXpJJwP7EWC4kxQOPqfR_JN1O"
        "03n" -> "https://drive.google.com/uc?export=download&id=1IEqp-bJ6xzxNv7kTRWzzDDS5rMp1uBYJ"
        "04d" -> "https://drive.google.com/uc?export=download&id=1jcplEvvp-Bk8FlTsLiz7PUbl_gtp0ZNX"
        "04n" -> "https://drive.google.com/uc?export=download&id=1IHLsdVW_v1K4u4V531PY-ag3XeGhkMG6"
        "09d" -> "https://drive.google.com/uc?export=download&id=1-LhsZ8liYxmOV7LEz5L7z88HGrXbOV7M"
        "09n" -> "https://drive.google.com/uc?export=download&id=1EXtYPlZeVyyrXICM8jfO0ghqdl9F9vlJ"
        "10d" -> "https://drive.google.com/uc?export=download&id=1M5pA8-5RDjr5m30-VJz8AKFI4_XpyKYd"
        "10n" -> "https://drive.google.com/uc?export=download&id=1gBZycRePYbXiCgs9mpXSq9vMkE3RMx9P"
        "11d" -> "https://drive.google.com/uc?export=download&id=1hMXfXIiFxLEHFYhnaWZ6a9Olwk8ibkMZ"
        "11n" -> "https://drive.google.com/uc?export=download&id=1CzMZEgmJLN08-WOEch0f2XTHlK9lp-c_"
        "13d" -> "https://drive.google.com/uc?export=download&id=1pRm6Fp908viJKva-wj2tGjVigqIDIwXV"
        "13n" -> "https://drive.google.com/uc?export=download&id=1pRm6Fp908viJKva-wj2tGjVigqIDIwXV"
        "50d" -> "https://drive.google.com/uc?export=download&id=1MHNqcLqkbevozYyC38PXVLg2g03lnFtB"
        "50n" -> "https://drive.google.com/uc?export=download&id=1MHNqcLqkbevozYyC38PXVLg2g03lnFtB"
        else -> ""
    }