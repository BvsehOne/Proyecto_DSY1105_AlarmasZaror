package com.fastservice.fastcatalog_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.fastservice.fastcatalog_grupox.viewmodel.AlertaViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PantallaPrincipalZaror(viewModel: AlertaViewModel) {
    val rolActual by viewModel.rolSeleccionado.observeAsState(initial = "Vecino/a")
    val listaAlertas by viewModel.alertas.observeAsState(initial = emptyList())
    val listaFallas by viewModel.fallas.observeAsState(initial = emptyList())

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Column {
                        Text(
                            text = "Alarmas Zaror",
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Poblacion Zaror - Cerrillos (425 Viviendas)",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primaryContainer,
                    titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
        ) {
            // Seleccionar Rol para probar los flujos
            Text(
                text = "Modo de Vista (Rol de Usuario):",
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                color = MaterialTheme.colorScheme.secondary
            )
            Spacer(modifier = Modifier.height(4.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                listOf("Vecino/a", "Directiva", "Encargado").forEach { rol ->
                    FilterChip(
                        selected = (rolActual == rol),
                        onClick = { viewModel.cambiarRol(rol) },
                        label = { Text(text = rol, fontSize = 12.sp) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            // Cambiar pantalla segun el rol seleccionado
            when (rolActual) {
                "Vecino/a" -> SeccionVecino(viewModel, listaAlertas)
                "Directiva" -> SeccionDirectiva(
                    alertas = listaAlertas,
                    onResolverAlerta = { alerta ->
                        viewModel.marcarAlertaComoResuelta(alerta.id)
                    }
                )
                "Encargado" -> SeccionEncargado(listaFallas)
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewPantanllaPrincipal() {
    PantallaPrincipalZaror(viewModel())
}