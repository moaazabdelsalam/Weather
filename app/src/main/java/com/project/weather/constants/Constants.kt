package com.project.weather.constants

object Constants {
    const val OWM_BASE_URL = "https://api.openweathermap.org/data/2.5/"
    const val NOMINATION_BASE_URL = "https://nominatim.openstreetmap.org/"
    const val CACHE_FILE_NAME = "weather_cache.txt"
    var cacheDirectory = ""
    const val DAY_OF_WEEK_KEY = "dayOfWeek"
    const val DAY_OF_MONTH_KEY = "dayOfMonth"
    const val MONTH_KEY = "month"
    const val YEAR_KEY = "year"
    const val TIME_KEY = "time"
    const val AM_PM_KEY = "am/pm"
    const val PREF_LOCATION_SOURCE = "home location source"
    const val PREF_LOCATION_GPS = "home location gps"
    const val PREF_LOCATION_MAP = "home location map"
}