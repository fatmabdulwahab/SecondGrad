package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL =
        "https://amr-yasserr-arsl-fingerspelling-detector.hf.space/"


    val api: SignApi by lazy {

        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(
                GsonConverterFactory.create()
            )
            .build()
            .create(SignApi::class.java)
    }
}