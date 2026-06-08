package com.example.secondgrad


import com.google.gson.annotations.SerializedName

data class SearchRouteRequest(
    @SerializedName("userLocation")
    val userLocation: String,

    @SerializedName("destination")
    val destination: String
)