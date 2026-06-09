package com.example.secondgrad

import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import java.io.File




class RouteRepository {


    suspend fun searchRoutes(
        request: SearchRouteRequest
    ): RouteResponse {
        return TransGuideRetrofit.api.searchRoutes(
            pageIndex = 1,
            pageSize = 10,
            request = request
        )
    }

    suspend fun sendVoice(file: File): ApiMessageResponse {
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
        val voicePart = MultipartBody.Part.createFormData(
            name = "file",
            filename = uploadName,
            body = requestBody
        )

        return TransGuideRetrofit.api.sendVoice(voicePart)
    }
}