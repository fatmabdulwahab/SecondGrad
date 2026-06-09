package com.example.secondgrad


import com.google.gson.annotations.SerializedName

data class SearchRouteRequest(
    @SerializedName("userLocation")
    val userLocation: String,

    @SerializedName("userLatitude")
    val userLatitude: Double? = null,

    @SerializedName("userLongitude")
    val userLongitude: Double? = null,

    @SerializedName("destination")
    val destination: String,

    @SerializedName("destinationLatitude")
    val destinationLatitude: Double? = null,

    @SerializedName("destinationLongitude")
    val destinationLongitude: Double? = null
)