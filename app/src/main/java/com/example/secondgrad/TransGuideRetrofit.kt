package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object TransGuideRetrofit {
    private const val BASE_URL = "https://transguideapi.runasp.net/"

    val api: TransGuideApi by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TransGuideApi::class.java)
    }
}
