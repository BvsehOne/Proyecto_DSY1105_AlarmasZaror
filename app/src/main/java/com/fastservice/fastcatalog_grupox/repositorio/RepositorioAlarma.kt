package com.fastservice.fastcatalog_grupox.repositorio

import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.modelo.FallaSistema

// Repositorio para la Poblacion Zaror - Inicia desde 0 para pruebas reales
class RepositorioAlarma {

    // Lista inicia vacía para crear pruebas desde 0 frente al profesor
    private val listaAlertas = mutableListOf<Alerta>()

    // Lista de fallas del sistema de alarmas
    private val listaFallas = mutableListOf(
        FallaSistema(1, "Sirena Sector 2", "Falla de batería de respaldo", "PENDIENTE"),
        FallaSistema(2, "Botonera Sede", "Falso contacto en botón principal", "EN REPARACIÓN")
    )

    fun obtenerAlertas(): List<Alerta> = listaAlertas.toList()

    fun agregarAlerta(alerta: Alerta) {
        listaAlertas.add(0, alerta)
    }

    fun obtenerFallas(): List<FallaSistema> = listaFallas.toList()
}
