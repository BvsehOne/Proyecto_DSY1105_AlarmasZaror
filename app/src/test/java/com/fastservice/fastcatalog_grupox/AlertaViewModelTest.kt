package com.fastservice.fastcatalog_grupox

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.fastservice.fastcatalog_grupox.viewmodel.AlertaViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Rule
import org.junit.Test

// Pruebas unitarias para validar la logica del ViewModel (Guía 15 - Unidad 3)
class AlertaViewModelTest {

    @get:Rule
    val instantTaskExecutorRule = InstantTaskExecutorRule()

    @Test
    fun validarFormulario_conDatosVacios_retornaFalso() {
        val viewModel = AlertaViewModel()
        
        val resultado = viewModel.validarFormulario("", "")
        
        assertFalse("El formulario deberia ser invalido", resultado)
        assertTrue("Debe existir un error en la ubicacion", viewModel.errorUbicacion != null)
        assertTrue("Debe existir un error en la descripcion", viewModel.errorDescripcion != null)
    }

    @Test
    fun validarFormulario_conDatosCorrectos_retornaVerdadero() {
        val viewModel = AlertaViewModel()
        
        val resultado = viewModel.validarFormulario("Los Jazmines #425", "Robo en la esquina")
        
        assertTrue("El formulario deberia ser valido", resultado)
        assertEquals(null, viewModel.errorUbicacion)
        assertEquals(null, viewModel.errorDescripcion)
    }
}
