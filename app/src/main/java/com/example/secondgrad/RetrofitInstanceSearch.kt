package com.example.secondgrad

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstanceSearch {
    private const val BASE_URL = "https://transguideapi.runasp.net/"

    private val retrofit: Retrofit = Retrofit.Builder()
        .baseUrl(BASE_URL)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    @Suppress("UNCHECKED_CAST")
    private val apiClass: Class<SearchApi> =
        Class.forName("com.example.secondgrad.SearchApi") as Class<SearchApi>

    val api: SearchApi = retrofit.create(apiClass)
}