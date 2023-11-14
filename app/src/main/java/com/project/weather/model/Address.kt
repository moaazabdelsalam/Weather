package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class Address(
    val city: String,
    val region: String,
    val country: String,
    @SerializedName("country_code")
    val countryCode: String
)
