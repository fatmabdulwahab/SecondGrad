package com.example.secondgrad


import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("pageIndex") val pageIndex: Int,
    @SerializedName("pageSize") val pageSize: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<RouteData>
)

data class RouteData(
    @SerializedName("routeName") val routeName: String,
    @SerializedName("routeType") val routeType: String,
    @SerializedName("routeLengthInKm") val routeLengthInKm: Double,
    @SerializedName("closestStationName") val closestStationName: String,
    @SerializedName("distanceToClosestStationKm") val distanceToClosestStationKm: Double,
    @SerializedName("transferStations") val transferStations: List<String>,
    @SerializedName("routeDetails") val routeDetails: List<RouteDetail>
)

data class RouteDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("routeName") val routeName: String,
    @SerializedName("ticketPrice") val ticketPrice: Int,
    @SerializedName("averageTimeInMinutes") val averageTimeInMinutes: Int,
    @SerializedName("stations") val stations: List<String>
)