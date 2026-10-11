package com.fastservice.fastcatalog_grupox.ui

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.content.ContextCompat
import com.fastservice.fastcatalog_grupox.modelo.Alerta
import com.fastservice.fastcatalog_grupox.modelo.FallaSistema
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
                "Directiva" -> SeccionDirectiva(listaAlertas)
                "Encargado" -> SeccionEncargado(listaFallas)
            }
        }
    }
}

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
                        onValueChange = { ubicacionTexto = it },
                        label = { Text("Ubicacion aprox. en Poblacion Zaror") },
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
                            viewModel.activarAlerta(
                                tipo = tipoSeleccionado,
                                ubicacion = ubicacionTexto,
                                descripcion = descripcionTexto,
                                fotoBitmap = fotoEvidencia,
                                latitud = latitudGps,
                                longitud = longitudGps
                            )
                            Toast.makeText(context, "¡ALERTA ENVIADA A LOS VECINOS!", Toast.LENGTH_LONG).show()
                            ubicacionTexto = ""
                            descripcionTexto = ""
                            fotoEvidencia = null
                            latitudGps = null
                            longitudGps = null
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

// Visor del mapa en Canvas para simular las 12 cuadras de la poblacion
@Composable
fun VisorMapaZaror(alertas: List<Alerta>) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(160.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // Dibujar calles
                for (i in 1..4) {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.4f),
                        start = Offset(w * (i / 5f), 0f),
                        end = Offset(w * (i / 5f), h),
                        strokeWidth = 6f
                    )
                }
                for (i in 1..3) {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.4f),
                        start = Offset(0f, h * (i / 4f)),
                        end = Offset(w, h * (i / 4f)),
                        strokeWidth = 6f
                    )
                }

                // Sede social en azul
                drawCircle(color = Color(0xFF1E88E5), radius = 10f, center = Offset(w * 0.5f, h * 0.5f))

                // Puntos rojos para las alertas activas
                alertas.filter { it.estado == "ACTIVA" }.forEachIndexed { index, _ ->
                    val posX = w * (0.2f + (index * 0.25f) % 0.7f)
                    val posY = h * (0.3f + (index * 0.3f) % 0.5f)
                    drawCircle(color = Color.Red, radius = 14f, center = Offset(posX, posY))
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                    .padding(6.dp)
            ) {
                Text("Mapa Sector Poblacion Zaror", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("Cerrillos - 12 Cuadras / 425 Viviendas", fontSize = 9.sp, color = Color.DarkGray)
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White.copy(alpha = 0.85f), RoundedCornerShape(6.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, RoundedCornerShape(4.dp))
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alertas Activas", fontSize = 9.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Vista de la directiva para revisar historial
@Composable
fun SeccionDirectiva(alertas: List<Alerta>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Resumen de la Directiva", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total de incidentes: ${alertas.size}", fontSize = 14.sp)
                    Text(text = "Alertas activas: ${alertas.count { it.estado == "ACTIVA" }}", fontSize = 14.sp)
                }
            }
        }

        item {
            Text(
                text = "Historial Completo de Incidentes:",
                fontWeight = FontWeight.Bold,
                fontSize = 16.sp
            )
        }

        items(alertas) { alerta ->
            TarjetaAlerta(alerta = alerta)
        }
    }
}

// Vista del encargado para revisar fallas
@Composable
fun SeccionEncargado(fallas: List<FallaSistema>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mantenimiento del Sistema de Alarmas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                    Text(text = "Monitoreo de sirenas y hardware del sector.", fontSize = 12.sp)
                }
            }
        }

        items(fallas) { falla ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Componente: ${falla.componente}", fontWeight = FontWeight.Bold)
                    Text(text = "Falla: ${falla.descripcion}", fontSize = 14.sp)
                    Text(text = "Estado: ${falla.estado}", fontSize = 12.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Tarjeta individual para cada alerta
@Composable
fun TarjetaAlerta(alerta: Alerta) {
    val colorEstado = when (alerta.estado) {
        "ACTIVA" -> MaterialTheme.colorScheme.error
        "EN ATENCION" -> Color(0xFFFF9800)
        else -> Color(0xFF4CAF50)
    }

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = alerta.tipo, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Box(
                    modifier = Modifier
                        .background(colorEstado, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                ) {
                    Text(
                        text = alerta.estado,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = alerta.ubicacion, fontSize = 13.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = alerta.descripcion, fontSize = 13.sp, color = Color.Gray)

            // Mostrar la foto capturada si existe
            alerta.fotoBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Evidencia Fotografica:", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(100.dp)
                        .border(1.dp, Color.LightGray, RoundedCornerShape(6.dp))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Hora: ${alerta.fechaHora}",
                fontSize = 11.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
