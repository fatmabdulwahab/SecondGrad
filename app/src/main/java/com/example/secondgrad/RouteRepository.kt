package com.example.secondgrad

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONObject
import java.io.File

class RouteRepository {

    private val httpClient = OkHttpClient()

    suspend fun searchRoutes(
        request: SearchRouteRequest
    ): RouteResponse {
        return TransGuideRetrofit.api.searchRoutes(
            pageIndex = 1,
            pageSize = 10,
            request = request
        )
    }

    suspend fun sendVoice(file: File): VoiceSendResponse {
        return withContext(Dispatchers.IO) {
            val isWavFile = file.name.endsWith(".wav")
            val mimeType = if (isWavFile) "audio/wav" else "audio/mp4"
            val uploadName = if (file.name.endsWith(".wav") || file.name.endsWith(".m4a")) {
                file.name
            } else if (isWavFile) {
                "${file.name}.wav"
            } else {
                "${file.name}.m4a"
            }

            val requestBody = file.asRequestBody(mimeType.toMediaTypeOrNull())
            val multipartBody = MultipartBody.Builder()
                .setType(MultipartBody.FORM)
                .addFormDataPart(
                    name = "file",
                    filename = uploadName,
                    body = requestBody
                )
                .build()

            val request = Request.Builder()
                .url(VOICE_API_URL)
                .post(multipartBody)
                .build()

            val response = httpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                val errorMessage = parseApiErrorMessage(responseBody)
                throw IllegalStateException(
                    if (errorMessage.length > 0) {
                        errorMessage
                    } else {
                        "مش قادرين نرفع الصوت (${response.code})"
                    }
                )
            }

            parseVoiceSendResponse(responseBody)
        }
    }

    private fun parseVoiceSendResponse(body: String): VoiceSendResponse {
        if (body.length == 0) {
            return VoiceSendResponse()
        }

        return try {
            val root = JSONObject(body)
            val message = root.optString("message", "")
            val status = root.optString("status", "")
            val success = root.optBoolean("success", false)
            val dataPayload = extractDataPayload(root)

            VoiceSendResponse(
                success = success,
                message = if (message.length > 0 && message != "null") message else null,
                status = if (status.length > 0 && status != "null") status else null,
                dataPayload = dataPayload
            )
        } catch (throwable: Throwable) {
            VoiceSendResponse(message = body)
        }
    }

    private fun extractDataPayload(root: JSONObject): String? {
        if (!root.has("data") || root.isNull("data")) {
            return null
        }

        val dataValue = root.get("data")
        if (dataValue == null || dataValue == JSONObject.NULL) {
            return null
        }

        if (dataValue is String) {
            val text = dataValue
            return if (text.length > 0 && text != "null") text else null
        }

        if (dataValue is JSONObject) {
            return dataValue.toString()
        }

        return dataValue.toString()
    }

    private fun parseApiErrorMessage(body: String): String {
        if (body.length == 0) {
            return ""
        }

        return try {
            val root = JSONObject(body)
            val errorMessage = root.optString("_errormessage", "")
            if (errorMessage.length > 0 && errorMessage != "null") {
                errorMessage
            } else {
                root.optString("message", "")
            }
        } catch (throwable: Throwable) {
            body
        }
    }

    companion object {
        private const val VOICE_API_URL = "https://transguideapi.runasp.net/api/Voice/SendVoice"
    }
}
