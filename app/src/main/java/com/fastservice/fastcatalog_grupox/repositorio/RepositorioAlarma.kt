package com.fastservice.fastcatalog_grupox.repositorio

import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.modelo.FallaSistema

// Repositorio donde manejamos la lista de alertas de la Poblacion Zaror
class RepositorioAlarma {

    // Lista de prueba para mostrar datos en la app
    private val listaAlertas = mutableListOf(
        Alerta(
            id = 1,
            tipo = "Robo / Asalto",
            fechaHora = "Hace 10 min",
            ubicacion = "Los Jazmines con Las Rosas",
            descripcion = "Intento de ingreso a vivienda vacia.",
            estado = "ACTIVA"
        ),
        Alerta(
            id = 2,
            tipo = "Emergencia Medica",
            fechaHora = "Hace 1 hora",
            ubicacion = "Pasaje Los Tulipanes #112",
            descripcion = "Adulto mayor necesita ayuda medica.",
            estado = "EN ATENCION"
        ),
        Alerta(
            id = 3,
            tipo = "Actividad Sospechosa",
            fechaHora = "Ayer 22:15",
            ubicacion = "Sede Vecinal - Los Jazmines #425",
            descripcion = "Auto sospechoso dando vueltas sin luces.",
            estado = "RESUELTA"
        )
    )

    // Lista de fallas del sistema de alarmas
    private val listaFallas = mutableListOf(
        FallaSistema(1, "Sirena Sector 2", "Falla de bateria", "PENDIENTE"),
        FallaSistema(2, "Botonera Sede", "Boton pegado", "EN REPARACION")
    )

    fun obtenerAlertas(): List<Alerta> = listaAlertas.toList()

    fun agregarAlerta(alerta: Alerta) {
        listaAlertas.add(0, alerta) // Agregar al inicio para verla arriba
    }

    fun obtenerFallas(): List<FallaSistema> = listaFallas.toList()

    fun actualizarEstadoAlerta(id: Int, nuevoEstado: String) {
        val index = listaAlertas.indexOfFirst { it.id == id }
        if (index != -1) {
            listaAlertas[index] = listaAlertas[index].copy(estado = nuevoEstado)
        }
    }


}
