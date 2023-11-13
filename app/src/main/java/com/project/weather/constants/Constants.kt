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
    const val PREF_LOCATION_GPS = "GPS"
    const val PREF_LOCATION_MAP = "MAP"
    const val PREF_LANGUAGE = "language"
    const val PREF_LANGUAGE_EN = "en"
    const val PREF_LANGUAGE_AR = "ar"
    const val PREF_TEMP_UNIT = "temperature"
    const val PREF_TEMP_C = "Celsius"
    const val PREF_TEMP_K = "Kelvin"
    const val PREF_TEMP_F = "Fahrenheit"
    const val PREF_SPEED_UNIT = "speed"
    const val PREF_SPEED_METER = "metre/sec"
    const val PREF_SPEED_MILE = "miles/hour"
    const val NOTIFICATION_PERMISSION_ID = 19
    const val FORECAST_NOTIFICATION_CHANNEL_ID = "74"
    const val FORECAST_NOTIFICATION_ID = 22
    const val ALERTS_NOTIFICATION_CHANNEL_ID = "1999"
    const val ALERTS_NOTIFICATION_ID = 99
    const val EXTRA_CITY_NAME_KEY = "city name"
    const val EXTRA_LAT_KEY = "latitude"
    const val EXTRA_LON_KEY = "longitude"
}