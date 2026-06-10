package com.example.secondgrad

import okhttp3.OkHttpClient
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit

object TransGuideRetrofit {

    private val httpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(ApiConfig.TRANSGUIDE_BASE_URL)
        .client(httpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Suppress("UNCHECKED_CAST")
    private val apiClass: Class<TransGuideApi> =
        Class.forName("com.example.secondgrad.TransGuideApi") as Class<TransGuideApi>

    val api: TransGuideApi = retrofit.create(apiClass)
}
