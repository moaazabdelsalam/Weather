package com.project.weather.model

import com.google.gson.annotations.SerializedName

data class ReverseNominationResponse(
    @SerializedName("display_name")
    val displayName: String,
    val address: Address,
    val namedetails: NameDetails
)
