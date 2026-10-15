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
        val estadoInicial = if (tipo == "Medica") "EN ATENCION" else "ACTIVA"

        val nuevaAlerta = Alerta(
            id = System.currentTimeMillis().toInt(),
            tipo = tipo,
            fechaHora = "Hace un momento",
            ubicacion = ubicacion,
            descripcion = descripcion.ifBlank { "Sin detalle adicional" },
            estado = estadoInicial,
            fotoBitmap = fotoBitmap,
            latitud = latitud,
            longitud = longitud
        )

        repositorio.agregarAlerta(nuevaAlerta)
        _alertas.value = repositorio.obtenerAlertas()
    }

    fun marcarAlertaComoResuelta(id: Int) {
        repositorio.actualizarEstadoAlerta(id, "RESUELTA")

        _alertas.value = repositorio.obtenerAlertas()
    }



}
