package com.fastservice.fastcatalog_grupox.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.ui.componse.TarjetaAlerta

// Vista de la directiva para revisar historial y gestionar
@Composable
fun SeccionDirectiva(
    alertas: List<Alerta>,
    onResolverAlerta: (Alerta) -> Unit
) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {

        // Panel de Resumen
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Resumen de la Directiva",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Total de incidentes registrados: ${alertas.size}",
                        fontSize = 14.sp
                    )
                    Text(
                        text = "Alertas actualmente activas: ${alertas.count { it.estado == "ACTIVA" }}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        // Título del Historial
        item {
            Text(
                text = "Historial Completo de Incidentes:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        // Lista de incidentes
        items(alertas) { alerta ->
            Column {
                TarjetaAlerta(alerta = alerta)

                if (alerta.estado != "RESUELTA") {
                    OutlinedButton(
                        onClick = { onResolverAlerta(alerta) },
                        colors = ButtonDefaults.outlinedButtonColors(
                            contentColor = Color(0xFF4CAF50)
                        ),
                        modifier = Modifier
                            .align(Alignment.End)
                            .padding(top = 4.dp)
                    ) {
                        Text("Marcar como Resuelta", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun PreviewSeccionDirectiva() {
    val alertasDePrueba = listOf(
        Alerta(
            id = 1,
            tipo = "Robo / Asalto",
            fechaHora = "14:30 - 13/10",
            ubicacion = "Los Jazmines #425",
            descripcion = "Individuo sospechoso rondando las casas.",
            estado = "ACTIVA"
        ),
        Alerta(
            id = 2,
            tipo = "Incendio",
            fechaHora = "10:15 - 12/10",
            ubicacion = "Pasaje Las Rosas #110",
            descripcion = "Humo saliendo del patio trasero.",
            estado = "RESUELTA"
        )
    )
    SeccionDirectiva(alertas = alertasDePrueba, onResolverAlerta = {})
}