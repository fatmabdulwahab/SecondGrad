package com.example.secondgrad

import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.UUID

class SignRepository {

    private val httpClient = OkHttpClient()

    fun createSession(): CreateSessionResponse {
        val apiSession = requestTransGuideSession()
        if (apiSession.resolvedSessionId().length > 0) {
            return apiSession
        }

        val localSessionId = UUID.randomUUID().toString()
        return CreateSessionResponse(session_id = localSessionId)
    }

    suspend fun recognize(
        request: RecognizeRequest
    ): RecognizeResponse {
        return RetrofitInstance.api.recognize(request)
    }

    fun endSession(
        sessionId: String
    ) {
        if (sessionId.length == 0) {
            return
        }

        var endedOnTransGuide = false

        try {
            val request = Request.Builder()
                .url("$TRANSGUIDE_BASE_URL/api/Sign/end/$sessionId")
                .post(ByteArray(0).toRequestBody(null))
                .build()

            val response = httpClient.newCall(request).execute()
            endedOnTransGuide = response.isSuccessful
            response.close()
        } catch (throwable: Throwable) {
            endedOnTransGuide = false
        }

    }

    private fun requestTransGuideSession(): CreateSessionResponse {
        return try {
            val request = Request.Builder()
                .url("$TRANSGUIDE_BASE_URL/api/Sign/create")
                .post("{}".toRequestBody(JSON_MEDIA_TYPE))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful || responseBody.length == 0) {
                return CreateSessionResponse()
            }

            parseCreateSessionResponse(responseBody)
        } catch (throwable: Throwable) {
            CreateSessionResponse()
        }
    }

    private fun parseCreateSessionResponse(body: String): CreateSessionResponse {
        return try {
            val root = JSONObject(body)
            val sessionId = root.optString("sessionId", "")
            val sessionIdSnake = root.optString("session_id", "")
            val success = root.optBoolean("success", false)

            CreateSessionResponse(
                sessionId = sessionId,
                session_id = sessionIdSnake,
                success = success
            )
        } catch (throwable: Throwable) {
            CreateSessionResponse()
        }
    }

    companion object {
        private const val TRANSGUIDE_BASE_URL = "https://transguideapi.runasp.net"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
