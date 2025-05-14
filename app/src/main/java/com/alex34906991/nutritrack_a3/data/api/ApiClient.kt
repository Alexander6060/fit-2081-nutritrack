package com.alex34906991.nutritrack_a3.data.api

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object ApiClient {
    private const val BASE_URL = "https://www.fruityvice.com/"
    
    private val retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    val fruityviceApi: FruityviceApi by lazy {
        retrofit.create(FruityviceApi::class.java)
    }
} 