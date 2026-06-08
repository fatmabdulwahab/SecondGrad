package com.example.secondgrad



import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query


interface SearchApi {

    @POST("api/Location/SearchRoutes")
    suspend fun searchRoutes(
        @Query("pageIndex") pageIndex: Int,
        @Query("pageSize") pageSize: Int,
        @Body  request: SearchRouteRequest
    ): RouteResponse
}