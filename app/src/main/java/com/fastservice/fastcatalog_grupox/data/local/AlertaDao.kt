package com.fastservice.fastcatalog_grupox.data.local


import androidx.room3.Dao
import androidx.room3.Insert
import androidx.room3.Query

@Dao
interface AlertaDao {
    @Query("SELECT * FROM alertas_local ORDER BY id DESC")
    fun obtenerAlertas(): List<AlertaEntity>

    @Insert
    fun insertarAlerta(alerta: AlertaEntity)
}