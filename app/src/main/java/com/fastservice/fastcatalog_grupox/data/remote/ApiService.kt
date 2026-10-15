package com.fastservice.fastcatalog_grupox.data.remote

import com.fastservice.fastcatalog_grupox.modelo.Alerta

// Interfaz para la conexion con el servidor backend en Spring Boot
interface ApiService {

    fun obtenerAlertasRemote(): List<Alerta>

    fun enviarAlertaRemote(alerta: Alerta): Alerta
}
