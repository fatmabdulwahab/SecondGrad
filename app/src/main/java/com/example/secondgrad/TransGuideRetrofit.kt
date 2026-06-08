package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java

object TransGuideRetrofit {
    private const val BASE_URL = "https://transguideapi.runasp.net/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val api: TransGuideApi = retrofit.create(TransGuideApi::class.java)
}
