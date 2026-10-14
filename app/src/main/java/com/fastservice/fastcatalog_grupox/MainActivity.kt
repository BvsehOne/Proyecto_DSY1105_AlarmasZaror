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

// MainActivity creada por Angelo Olivares - Proyecto Alarmas Zaror
class MainActivity : ComponentActivity() {

    private val viewModel: AlertaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
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

    // Cliente de Ktor
    private val client = HttpClient(CIO)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Llamamos a la API
        probarApi()
    }

    private fun probarApi() {
        GlobalScope.launch {
            try {
                // IMPORTANTE: Usar 10.0.2.2 para que el emulador llegue a tu PC
                val respuesta: HttpResponse = client.get("http://10.0.2.2:8080/api/hola")
                val textoRespuesta = respuesta.bodyAsText()

                // Muestra el resultado en el Logcat de Android Studio
                Log.d("API_TEST", "Respuesta de la API: $textoRespuesta")
            } catch (e: Exception) {
                Log.e("API_TEST", "Error conectando a la API", e)
            }
        }
    }
}