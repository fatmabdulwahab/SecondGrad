package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory


object RetrofitInstance {

    private const val BASE_URL =
        "https://amr-yasserr-arsl-fingerspelling-detector.hf.space/"


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    @Suppress("UNCHECKED_CAST")
    private val apiClass: Class<SignApi> =
        Class.forName("com.example.secondgrad.SignApi") as Class<SignApi>

    val api: SignApi = retrofit.create(apiClass)
}