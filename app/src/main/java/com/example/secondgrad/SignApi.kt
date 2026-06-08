package com.example.secondgrad

import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Path

interface SignApi {


    @POST("api/sign/create")
    suspend fun createSession(): CreateSessionResponse

    @POST("api/v1/recognize")
    suspend fun recognize(
        @Body request: RecognizeRequest
    ): RecognizeResponse

    @POST("api/sign/end/{sessionId}")
    suspend fun endSession(
        @Path("sessionId") sessionId: String
    )


}