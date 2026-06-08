package com.example.secondgrad



class SignRepository {

    suspend fun createSession(): CreateSessionResponse {
        return RetrofitInstance.api.createSession()
    }

    suspend fun recognize(
        request: RecognizeRequest
    ): RecognizeResponse {
        return RetrofitInstance.api.recognize(request)
    }

    suspend fun endSession(
        sessionId: String
    ) {
        RetrofitInstance.api.endSession(sessionId)
    }
}