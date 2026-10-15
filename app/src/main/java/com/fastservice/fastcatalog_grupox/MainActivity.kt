package com.fastservice.fastcatalog_grupox

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import com.fastservice.fastcatalog_grupox.ui.screens.PantallaPrincipalZaror
import com.fastservice.fastcatalog_grupox.ui.theme.FastCatalog_GrupoXTheme
import com.fastservice.fastcatalog_grupox.viewmodel.AlertaViewModel
import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import android.util.Log

// MainActivity creada por Angelo Olivares - Proyecto Alarmas Zaror
class MainActivity : ComponentActivity() {

    private val viewModel: AlertaViewModel by viewModels()
    private val client = HttpClient(CIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // LOG DE PRUEBA INMEDIATO
        android.util.Log.d("API_TEST", ">>> LA APLICACIÓN SE ABRIÓ CORRECTAMENTE <<<")

        // Llamamos a la API al iniciar la actividad
        probarApi()

        setContent {
            FastCatalog_GrupoXTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    PantallaPrincipalZaror(viewModel)
                }
            }
        }
    }

    private fun probarApi() {
        Log.d("API_TEST", "Intentando conectar a la API...")
        GlobalScope.launch {
            try {
                val respuesta: HttpResponse = client.get("http://ip:8080/api/hola")
                val textoRespuesta = respuesta.bodyAsText()
                Log.d("API_TEST", "Respuesta de la API: $textoRespuesta")
            } catch (e: Exception) {
                Log.e("API_TEST", "Error conectando a la API", e)
            }
        }
    }
}