package com.fastservice.fastcatalog_grupox.ui.screens

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.ui.componse.VisorMapaZaror
import com.fastservice.fastcatalog_grupox.ui.componse.TarjetaAlerta
import com.fastservice.fastcatalog_grupox.viewmodel.AlertaViewModel

// Pantalla principal del vecino para activar la alarma
@Composable
fun SeccionVecino(viewModel: AlertaViewModel, alertas: List<Alerta>) {
    val context = LocalContext.current
    var tipoSeleccionado by remember { mutableStateOf("Robo / Asalto") }
    var ubicacionTexto by remember { mutableStateOf("") }
    var descripcionTexto by remember { mutableStateOf("") }
    var fotoEvidencia by remember { mutableStateOf<Bitmap?>(null) }
    var latitudGps by remember { mutableStateOf<Double?>(null) }
    var longitudGps by remember { mutableStateOf<Double?>(null) }

    var errorUbicacion by remember { mutableStateOf(false) }
    // Abrir la camara para tomar foto
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoEvidencia = bitmap
            Toast.makeText(context, "Foto de evidencia tomada", Toast.LENGTH_SHORT).show()
        }
    }

    // Pedir permiso de camara
    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcherCamara.launch(null)
        } else {
            Toast.makeText(context, "Permiso de camara no aceptado", Toast.LENGTH_SHORT).show()
        }
    }

    // Pedir permiso de GPS
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            latitudGps = -33.5042
            longitudGps = -70.7185
            ubicacionTexto = "Los Jazmines #425 (GPS: -33.5042, -70.7185)"
            errorUbicacion = false
            Toast.makeText(context, "Ubicacion GPS obtenida", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de GPS no aceptado", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            // Mapa dibujado con Canvas para mostrar el sector
            VisorMapaZaror(alertas)
        }

        item {
            // Formulario para activar la alarma de emergencia
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alerta",
                        tint = MaterialTheme.colorScheme.error,
                        modifier = Modifier.size(40.dp)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "ACTIVAR ALARMA COMUNITARIA",
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        fontSize = 17.sp
                    )
                    Text(
                        text = "Envia una alerta urgente a los vecinos del sector",
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Filtros para elegir el tipo de emergencia
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Robo / Asalto", "Incendio", "Medica", "Sospecha").forEach { tipo ->
                            FilterChip(
                                selected = (tipoSeleccionado == tipo),
                                onClick = { tipoSeleccionado = tipo },
                                label = { Text(tipo, fontSize = 11.sp) }
                            )
                        }
                    }

                    OutlinedTextField(
                        value = ubicacionTexto,
                        onValueChange = {
                            ubicacionTexto = it
                            errorUbicacion = false
                                        },
                        label = { Text("Ubicacion aprox. en Poblacion Zaror") },
                        isError = errorUbicacion,
                        supportingText = {
                            if (errorUbicacion) {
                                Text(
                                    text = "La ubicación es obligatoria",
                                    color = MaterialTheme.colorScheme.error
                                )
                            }
                        },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(6.dp))

                    OutlinedTextField(
                        value = descripcionTexto,
                        onValueChange = { descripcionTexto = it },
                        label = { Text("Detalle de lo sucedido") },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botones de Camara y GPS
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED) {
                                    launcherCamara.launch(null)
                                } else {
                                    launcherPermisoCamara.launch(Manifest.permission.CAMERA)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Camara", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Tomar Foto", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    latitudGps = -33.5042
                                    longitudGps = -70.7185
                                    ubicacionTexto = "Los Jazmines #425 (GPS: -33.5042, -70.7185)"
                                    errorUbicacion = false
                                    Toast.makeText(context, "GPS obtenido", Toast.LENGTH_SHORT).show()
                                } else {
                                    launcherPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "GPS", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Usar GPS", fontSize = 11.sp)
                        }
                    }

                    // Muestra la foto si fue tomada
                    fotoEvidencia?.let { bitmap ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Evidencia",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Button(
                        onClick = {
                            if (ubicacionTexto.isBlank()) {
                                errorUbicacion = true
                                Toast.makeText(context, "Debe ingresar una ubicación", Toast.LENGTH_SHORT).show()
                            } else {
                                viewModel.activarAlerta(
                                    tipo = tipoSeleccionado,
                                    ubicacion = ubicacionTexto,
                                    descripcion = descripcionTexto,
                                    fotoBitmap = fotoEvidencia,
                                    latitud = latitudGps,
                                    longitud = longitudGps
                                )
                                Toast.makeText(context, "¡ALERTA ENVIADA A LOS VECINOS!", Toast.LENGTH_LONG).show()

                                // Limpiar los campos después del envío exitoso
                                ubicacionTexto = ""
                                descripcionTexto = ""
                                fotoEvidencia = null
                                latitudGps = null
                                longitudGps = null
                                errorUbicacion = false
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text("EMITIR ALERTA AHORA", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        item {
            Text(
                text = "Alertas Recientes en el Sector:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(alertas) { alerta ->
            TarjetaAlerta(alerta = alerta)
        }
    }
}


@Composable
@Preview(showBackground = true)
fun PreviewSeccionVecino() {
    SeccionVecino(
        viewModel = AlertaViewModel(),
        alertas = emptyList()
    )
}