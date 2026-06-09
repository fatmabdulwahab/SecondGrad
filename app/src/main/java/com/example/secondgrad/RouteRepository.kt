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
        val requestBody = file.asRequestBody("audio/mp4".toMediaTypeOrNull())
        val voicePart = MultipartBody.Part.createFormData(
            name = "file",
            filename = file.name,
            body = requestBody
        )

        return TransGuideRetrofit.api.sendVoice(voicePart)
    }
}