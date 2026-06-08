package com.example.secondgrad


import com.google.gson.annotations.SerializedName

data class RouteResponse(
    @SerializedName("page_index") val pageIndex: Int,
    @SerializedName("page_size") val pageSize: Int,
    @SerializedName("count") val count: Int,
    @SerializedName("data") val data: List<RouteData>
)

data class RouteData(
    @SerializedName("route_name") val routeName: String,
    @SerializedName("route_type") val routeType: String,
    @SerializedName("route_length_in_km") val routeLengthInKm: Double,
    @SerializedName("closest_station_name") val closestStationName: String,
    @SerializedName("distance_to_closest_station_km") val distanceToClosestStationKm: Double,
    @SerializedName("transfer_stations") val transferStations: List<String>,
    @SerializedName("route_details") val routeDetails: List<RouteDetail>
)

data class RouteDetail(
    @SerializedName("id") val id: Int,
    @SerializedName("route_name") val routeName: String,
    @SerializedName("ticket_price") val ticketPrice: Int,
    @SerializedName("average_time_in_minutes") val averageTimeInMinutes: Int,
    @SerializedName("stations") val stations: List<String>
)
// mapping
data class RouteResponse2(
    val routeType: String,
    val routeName: String,
    val ticketPrice: Int,
    val averageTimeInMinutes: Int,
    val closestStationName: String,
    val transferStations: List<String>,

    )