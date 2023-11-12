package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class NameDetails(
    val name: String,
    @SerializedName("name:ar")
    val nameAr: String,
    @SerializedName("name:en")
    val nameEn: String
)