package com.fastservice.fastcatalog_grupox.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room3.vo.PrimaryKey

// Implementacion de Room por Marcelo Pincheira
@Entity(tableName = "alertas_local")
data class AlertaEntity(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val tipo: String,
    val fechaHora: String,
    val ubicacion: String,
    val descripcion: String,
    val estado: String = "ACTIVA"
)