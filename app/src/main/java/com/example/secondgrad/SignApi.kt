package com.example.secondgrad

import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.POST
import retrofit2.http.Path

interface SignApi {

    @POST("api/v1/recognize")
    suspend fun recognize(
        @Body request: RecognizeRequest
    ): RecognizeResponse

    @DELETE("api/v1/session/{sessionId}")
    suspend fun endSession(
        @Path("sessionId") sessionId: String
    )
}
