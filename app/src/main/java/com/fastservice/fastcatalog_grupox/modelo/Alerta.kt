package com.fastservice.fastcatalog_grupox.modelo

import android.graphics.Bitmap

// Clase para guardar los datos de cada alerta que envian los vecinos
data class Alerta(
    val id: Int,
    val tipo: String,           // Ejemplo: Robo, Incendio, Emergencia Medica
    val fechaHora: String,      // Fecha u hora del incidente
    val ubicacion: String,      // Direccion o pasaje de la poblacion Zaror
    val descripcion: String,    // Breve detalle de lo que paso
    val estado: String = "ACTIVA", // ACTIVA, EN ATENCION, RESUELTA
    val fotoBitmap: Bitmap? = null, // Foto capturada con la camara
    val latitud: Double? = null,
    val longitud: Double? = null
)

// Clase para el reporte de fallas de la alarma (para el encargado)
data class FallaSistema(
    val id: Int,
    val componente: String,
    val descripcion: String,
    val estado: String = "PENDIENTE"
)
