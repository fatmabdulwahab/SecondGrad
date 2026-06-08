package com.example.secondgrad


import com.google.gson.annotations.SerializedName

data class SearchRouteRequest(
    @SerializedName("user_location")
    val userLocation: String,

    @SerializedName("destination")
    val destination: String
)