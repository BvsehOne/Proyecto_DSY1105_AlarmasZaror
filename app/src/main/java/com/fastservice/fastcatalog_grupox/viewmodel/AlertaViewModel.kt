package com.fastservice.fastcatalog_grupox.viewmodel

import android.graphics.Bitmap
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
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

    // LiveData para la lista de alertas
    private val _alertas = MutableLiveData<List<Alerta>>()
    val alertas: LiveData<List<Alerta>> get() = _alertas

    // LiveData para las fallas reportadas
    private val _fallas = MutableLiveData<List<FallaSistema>>()
    val fallas: LiveData<List<FallaSistema>> get() = _fallas

    // Rol seleccionado (Vecino/a, Directiva, Encargado)
    private val _rolSeleccionado = MutableLiveData("Vecino/a")
    val rolSeleccionado: LiveData<String> get() = _rolSeleccionado

    // Estados de Validacion de Formulario desacoplados de la UI (Cumplimiento IE 2.2.1)
    var errorUbicacion by mutableStateOf<String?>(null)
        private set

    var errorDescripcion by mutableStateOf<String?>(null)
        private set

    var estadoUltimaAccion by mutableStateOf<String?>(null)
        private set

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

    // Validacion desacoplada de la UI
    fun validarFormulario(ubicacion: String, descripcion: String): Boolean {
        var esValido = true

        if (ubicacion.trim().length < 4) {
            errorUbicacion = "Ingresa una dirección válida (mínimo 4 caracteres)"
            esValido = false
        } else {
            errorUbicacion = null
        }

        if (descripcion.trim().length < 5) {
            errorDescripcion = "El detalle debe tener al menos 5 caracteres"
            esValido = false
        } else {
            errorDescripcion = null
        }

        return esValido
    }

    fun activarAlerta(
        tipo: String,
        ubicacion: String,
        descripcion: String,
        fotoBitmap: Bitmap? = null,
        latitud: Double? = null,
        longitud: Double? = null
    ): Boolean {
        if (!validarFormulario(ubicacion, descripcion)) {
            return false
        }

        val formatoHora = SimpleDateFormat("HH:mm - dd/MM", Locale.getDefault())
        val horaActual = formatoHora.format(Date())

        val nuevaAlerta = Alerta(
            id = (System.currentTimeMillis() % 10000).toInt(),
            tipo = tipo,
            fechaHora = horaActual,
            ubicacion = ubicacion.trim(),
            descripcion = descripcion.trim(),
            estado = "ACTIVA",
            fotoBitmap = fotoBitmap,
            latitud = latitud,
            longitud = longitud
        )

        repositorio.agregarAlerta(nuevaAlerta)
        _alertas.value = repositorio.obtenerAlertas()
        estadoUltimaAccion = "Alerta $tipo emitida con éxito"
        return true
    }

    fun limpiarErrores() {
        errorUbicacion = null
        errorDescripcion = null
    }
}
