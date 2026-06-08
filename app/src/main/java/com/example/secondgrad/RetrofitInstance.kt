package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import kotlin.jvm.java


object RetrofitInstance {

    private const val BASE_URL =
        "https://amr-yasserr-arsl-fingerspelling-detector.hf.space/"


    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(
            GsonConverterFactory.create()
        )
        .build()

    val api: SignApi = retrofit.create(SignApi::class.java)
}