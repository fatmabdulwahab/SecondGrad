package com.example.secondgrad

import java.util.UUID

class SignRepository {

    fun createSession(): CreateSessionResponse {
        val sessionId = UUID.randomUUID().toString()
        return CreateSessionResponse(session_id = sessionId)
    }

    suspend fun recognize(
        request: RecognizeRequest
    ): RecognizeResponse {
        return RetrofitInstance.api.recognize(request)
    }

    suspend fun endSession(
        sessionId: String
    ) {
        if (sessionId.length > 0) {
            RetrofitInstance.api.endSession(sessionId)
        }
    }
}
