package com.fastservice.fastcatalog_grupox.data.remote

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// Cliente Retrofit creado para conectar con el Microservicio Spring Boot (Unidad 3)
object RetrofitClient {
    // 10.0.2.2 es la IP especial del emulador Android para conectar al localhost de tu PC (Spring Boot)
    private const val BASE_URL = "http://10.0.2.2:8080/"

    val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }
}
