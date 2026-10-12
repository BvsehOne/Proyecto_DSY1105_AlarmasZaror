package com.fastservice.fastcatalog_grupox.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlertaDao {
    @Query("SELECT * FROM alertas_local ORDER BY id DESC")
    fun obtenerAlertas(): List<AlertaEntity>

    @Insert
    fun insertarAlerta(alerta: AlertaEntity)
}