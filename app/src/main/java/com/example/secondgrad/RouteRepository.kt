package com.example.secondgrad




class RouteRepository {


    suspend fun searchRoutes(
        request: SearchRouteRequest
    ): RouteResponse {
        return RetrofitInstanceSearch.api.searchRoutes(
            pageIndex = 1,
            pageSize = 10,
            request = request
        )
    }
}