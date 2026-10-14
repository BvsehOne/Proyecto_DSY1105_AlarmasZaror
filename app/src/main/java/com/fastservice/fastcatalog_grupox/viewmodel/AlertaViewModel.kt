package com.fastservice.fastcatalog_grupox.viewmodel

import android.graphics.Bitmap
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.modelo.FallaSistema
import com.fastservice.fastcatalog_grupox.repositorio.RepositorioAlarma
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AlertaViewModel : ViewModel() {

    private val repositorio = RepositorioAlarma()

    // LiveData para las alertas
    private val _alertas = MutableLiveData<List<Alerta>>()
    val alertas: LiveData<List<Alerta>> get() = _alertas

    // LiveData para las fallas
    private val _fallas = MutableLiveData<List<FallaSistema>>()
    val fallas: LiveData<List<FallaSistema>> get() = _fallas

    // Rol seleccionado (Vecino/a, Directiva, Encargado)
    private val _rolSeleccionado = MutableLiveData("Vecino/a")
    val rolSeleccionado: LiveData<String> get() = _rolSeleccionado

    init {
        cargarDatos()
    }

    private fun cargarDatos() {
        _alertas.value = repositorio.obtenerAlertas()
        _fallas.value = repositorio.obtenerFallas()
    }

    fun cambiarRol(nuevoRol: String) {
        _rolSeleccionado.value = nuevoRol
    }

    // Funcion para crear y agregar una nueva alerta desde la pantalla
    fun activarAlerta(
        tipo: String,
        ubicacion: String,
        descripcion: String,
        fotoBitmap: Bitmap? = null,
        latitud: Double? = null,
        longitud: Double? = null
    ) {
        val formatoHora = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
        val horaActual = formatoHora.format(Date())

        val nuevaAlerta = Alerta(
            id = (System.currentTimeMillis() % 10000).toInt(),
            tipo = tipo,
            fechaHora = horaActual,
            ubicacion = ubicacion.ifBlank { "Los Jazmines #425 (Sector Zaror)" },
            descripcion = descripcion.ifBlank { "Alarma activada desde la App" },
            estado = "ACTIVA",
            fotoBitmap = fotoBitmap,
            latitud = latitud,
            longitud = longitud
        )

        repositorio.agregarAlerta(nuevaAlerta)
        _alertas.value = repositorio.obtenerAlertas()
    }

    fun marcarAlertaComoResuelta(id: Int) {
        // 1. Modifica la alerta dentro del repositorio
        repositorio.actualizarEstadoAlerta(id, "RESUELTA")

        // 2. Notifica a la interfaz con la lista actualizada
        _alertas.value = repositorio.obtenerAlertas()
    }

}
