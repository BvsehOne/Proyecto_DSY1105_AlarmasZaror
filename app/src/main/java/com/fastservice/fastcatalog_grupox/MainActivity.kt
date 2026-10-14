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
}