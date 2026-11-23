package com.fastservice.fastcatalog_grupox.data.remote

import com.fastservice.fastcatalog_grupox.modelo.Alerta
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.POST

// Endpoints de la API REST (Unidad 3 - Guía 14)
interface ApiService {

    @GET("api/alertas")
    suspend fun obtenerAlertas(): List<Alerta>

    @POST("api/alertas")
    suspend fun registrarAlerta(@Body alerta: Alerta): Alerta
}
