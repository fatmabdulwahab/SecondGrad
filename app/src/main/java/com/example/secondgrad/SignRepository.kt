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

    fun endSession(sessionId: String): EndSessionResult {
        if (sessionId.length == 0) {
            return EndSessionResult(message = "No active session")
        }

        endHfSession(sessionId)
        return endTransGuideSession(sessionId)
    }

    private fun endHfSession(sessionId: String) {
        try {
            val request = Request.Builder()
                .url("$HF_BASE_URL/api/v1/session/$sessionId")
                .delete()
                .build()

            httpClient.newCall(request).execute().close()
        } catch (throwable: Throwable) {
            // HF session cleanup is best-effort.
        }
    }

    private fun endTransGuideSession(sessionId: String): EndSessionResult {
        return try {
            val request = Request.Builder()
                .url("$TRANSGUIDE_BASE_URL/api/Sign/end/$sessionId")
                .post(ByteArray(0).toRequestBody(null))
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""
            response.close()

            if (!response.isSuccessful) {
                return EndSessionResult(
                    success = false,
                    message = "Failed to end session (${response.code})",
                    sessionId = sessionId
                )
            }

            parseEndSessionResponse(responseBody, sessionId)
        } catch (throwable: Throwable) {
            EndSessionResult(
                success = false,
                message = throwable.message ?: "Failed to end session",
                sessionId = sessionId
            )
        }
    }

    private fun parseEndSessionResponse(body: String, fallbackSessionId: String): EndSessionResult {
        return try {
            val root = JSONObject(body)
            EndSessionResult(
                success = root.optBoolean("success", true),
                message = root.optString("message", root.optString("detail", "Session ended")),
                sessionId = root.optString("sessionId", fallbackSessionId)
            )
        } catch (throwable: Throwable) {
            EndSessionResult(
                success = true,
                message = if (body.length > 0) body else "Session ended",
                sessionId = fallbackSessionId
            )
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
        private const val TRANSGUIDE_BASE_URL =
            "https://transguideapi.runasp.net"
        private const val HF_BASE_URL =
            "https://amr-yasserr-arsl-fingerspelling-detector.hf.space"
        private val JSON_MEDIA_TYPE = "application/json; charset=utf-8".toMediaType()
    }
}
