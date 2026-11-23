package com.fastservice.fastcatalog_grupox.ui

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
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
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.MarkerState
import com.google.maps.android.compose.rememberCameraPositionState
import kotlinx.coroutines.launch
import java.util.Locale

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
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 20.sp
                        )
                        Text(
                            text = "Junta de Vecinos Cerrillos • 425 Viviendas",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onPrimary.copy(alpha = 0.85f)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Seleccionar Rol (Perfil Activo con interactividad cruzada)
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                        RoundedCornerShape(12.dp)
                    )
                    .padding(4.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                listOf("Vecino/a", "Directiva", "Encargado").forEach { rol ->
                    val esSeleccionado = (rolActual == rol)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(10.dp))
                            .background(
                                if (esSeleccionado) MaterialTheme.colorScheme.primary
                                else Color.Transparent
                            )
                            .clickable { viewModel.cambiarRol(rol) }
                            .padding(vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = rol,
                            fontSize = 12.sp,
                            fontWeight = if (esSeleccionado) FontWeight.Bold else FontWeight.Medium,
                            color = if (esSeleccionado) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (rolActual) {
                "Vecino/a" -> SeccionVecino(viewModel, listaAlertas)
                "Directiva" -> SeccionDirectiva(viewModel, listaAlertas)
                "Encargado" -> SeccionEncargado(viewModel, listaFallas, listaAlertas)
            }
        }
    }
}

@Composable
fun SeccionVecino(viewModel: AlertaViewModel, alertas: List<Alerta>) {
    val context = LocalContext.current
    var tipoSeleccionado by remember { mutableStateOf("Robo / Asalto") }
    var ubicacionTexto by remember { mutableStateOf("") }
    var descripcionTexto by remember { mutableStateOf("") }
    var fotoEvidencia by remember { mutableStateOf<Bitmap?>(null) }
    var latitudGps by remember { mutableStateOf<Double?>(null) }
    var longitudGps by remember { mutableStateOf<Double?>(null) }
    var mostrarBannerExito by remember { mutableStateOf(false) }

    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()
    val fusedLocationClient = remember { LocationServices.getFusedLocationProviderClient(context) }

    // Launcher para la Cámara
    val launcherCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicturePreview()
    ) { bitmap ->
        if (bitmap != null) {
            fotoEvidencia = bitmap
            Toast.makeText(context, "Evidencia fotográfica adjuntada", Toast.LENGTH_SHORT).show()
        }
    }

    val launcherPermisoCamara = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            launcherCamara.launch(null)
        } else {
            Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
        }
    }

    // Launcher para Permiso de Ubicación GPS Real
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            try {
                fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                    if (location != null) {
                        latitudGps = location.latitude
                        longitudGps = location.longitude
                        ubicacionTexto = "Población Zaror, Cerrillos (GPS Real: ${String.format(Locale.getDefault(), "%.4f", location.latitude)}, ${String.format(Locale.getDefault(), "%.4f", location.longitude)})"
                        viewModel.limpiarErrores()
                        Toast.makeText(context, "¡Ubicación GPS real obtenida con éxito!", Toast.LENGTH_SHORT).show()
                    } else {
                        latitudGps = -33.5042
                        longitudGps = -70.7185
                        ubicacionTexto = "Los Jazmines #425, Cerrillos (Sector Zaror)"
                        Toast.makeText(context, "Ubicación referencial Zaror", Toast.LENGTH_SHORT).show()
                    }
                }
            } catch (e: SecurityException) {
                latitudGps = -33.5042
                longitudGps = -70.7185
                ubicacionTexto = "Los Jazmines #425, Cerrillos (Sector Zaror)"
            }
        } else {
            Toast.makeText(context, "Permiso de GPS denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(
        state = listState,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            // Mapa Vectorial GPS de Alta Precisión (Con interacción y scroll automático al formulario)
            VisorGoogleMapsZaror(
                alertas = alertas,
                gpsActivo = latitudGps != null,
                latActual = latitudGps ?: -33.5042,
                lngActual = longitudGps ?: -70.7185,
                onUbicacionSeleccionada = { nuevaUbicacion ->
                    ubicacionTexto = nuevaUbicacion
                    viewModel.limpiarErrores()
                    coroutineScope.launch {
                        listState.animateScrollToItem(1)
                    }
                    Toast.makeText(context, "📍 Ubicación exacta fijada en el formulario", Toast.LENGTH_LONG).show()
                }
            )
        }

        item {
            // Animacion de Retroalimentación de Éxito
            AnimatedVisibility(
                visible = mostrarBannerExito,
                enter = slideInVertically() + fadeIn(),
                exit = slideOutVertically() + fadeOut()
            ) {
                Card(
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFE8F5E9)),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 6.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Alerta registrada desde 0 en el sistema!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            // Formulario Validado de Emergencia
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF5F5)),
                elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(300))
                    .border(1.dp, Color(0xFFFFCDD2), RoundedCornerShape(16.dp))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(38.dp)
                                .background(MaterialTheme.colorScheme.error, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = "Alerta",
                                tint = Color.White,
                                modifier = Modifier.size(22.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(10.dp))
                        Column {
                            Text(
                                text = "BOTÓN DE PÁNICO Y LLAMADA RÁPIDA",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 14.sp
                            )
                            Text(
                                text = "Toca el mapa, usa GPS o llama a emergencias",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tipos de Incidente (Grilla 2x2 para evitar deformaciones)
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Robo / Asalto", "Incendio").forEach { tipo ->
                                val seleccionado = (tipoSeleccionado == tipo)
                                FilterChip(
                                    selected = seleccionado,
                                    onClick = { tipoSeleccionado = tipo },
                                    label = { Text(tipo, fontSize = 11.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium) },
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                                )
                            }
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            listOf("Médica", "Sospecha").forEach { tipo ->
                                val seleccionado = (tipoSeleccionado == tipo)
                                FilterChip(
                                    selected = seleccionado,
                                    onClick = { tipoSeleccionado = tipo },
                                    label = { Text(tipo, fontSize = 11.sp, fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium) },
                                    modifier = Modifier.weight(1f).padding(horizontal = 2.dp)
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Campo 1: Ubicacion
                    OutlinedTextField(
                        value = ubicacionTexto,
                        onValueChange = {
                            ubicacionTexto = it
                            if (it.length >= 4) viewModel.limpiarErrores()
                        },
                        label = { Text("Ubicación exacta (Toca el mapa o usa GPS)") },
                        isError = viewModel.errorUbicacion != null,
                        trailingIcon = {
                            if (viewModel.errorUbicacion != null) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        supportingText = {
                            viewModel.errorUbicacion?.let { msg ->
                                Text(text = msg, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(4.dp))

                    // Campo 2: Detalle
                    OutlinedTextField(
                        value = descripcionTexto,
                        onValueChange = {
                            descripcionTexto = it
                            if (it.length >= 5) viewModel.limpiarErrores()
                        },
                        label = { Text("Detalle de la emergencia") },
                        isError = viewModel.errorDescripcion != null,
                        trailingIcon = {
                            if (viewModel.errorDescripcion != null) {
                                Icon(Icons.Default.Warning, contentDescription = "Error", tint = MaterialTheme.colorScheme.error)
                            }
                        },
                        supportingText = {
                            viewModel.errorDescripcion?.let { msg ->
                                Text(text = msg, color = MaterialTheme.colorScheme.error, fontSize = 11.sp)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botones de Recursos Nativos (Cámara + GPS)
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
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Foto Evidencia", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    try {
                                        fusedLocationClient.lastLocation.addOnSuccessListener { location ->
                                            if (location != null) {
                                                latitudGps = location.latitude
                                                longitudGps = location.longitude
                                                ubicacionTexto = "Población Zaror, Cerrillos (GPS Real: ${String.format(Locale.getDefault(), "%.4f", location.latitude)}, ${String.format(Locale.getDefault(), "%.4f", location.longitude)})"
                                                viewModel.limpiarErrores()
                                                Toast.makeText(context, "¡GPS real obtenido con éxito!", Toast.LENGTH_SHORT).show()
                                            } else {
                                                latitudGps = -33.5042
                                                longitudGps = -70.7185
                                                ubicacionTexto = "Los Jazmines #425, Cerrillos (GPS Zaror)"
                                                Toast.makeText(context, "Ubicación referencial Zaror", Toast.LENGTH_SHORT).show()
                                            }
                                        }
                                    } catch (e: SecurityException) {
                                        latitudGps = -33.5042
                                        longitudGps = -70.7185
                                        ubicacionTexto = "Los Jazmines #425, Cerrillos (Sector Zaror)"
                                    }
                                } else {
                                    launcherPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Obtener GPS", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                        }
                    }

                    // Previsualizacion de Foto
                    fotoEvidencia?.let { bitmap ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Evidencia Capturada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(120.dp)
                                .clip(RoundedCornerShape(10.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(10.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Boton Registrar Incidencia
                    Button(
                        onClick = {
                            val exito = viewModel.activarAlerta(
                                tipo = tipoSeleccionado,
                                ubicacion = ubicacionTexto,
                                descripcion = descripcionTexto,
                                fotoBitmap = fotoEvidencia,
                                latitud = latitudGps,
                                longitud = longitudGps
                            )

                            if (exito) {
                                mostrarBannerExito = true
                                ubicacionTexto = ""
                                descripcionTexto = ""
                                fotoEvidencia = null
                                latitudGps = null
                                longitudGps = null
                            }
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Text("EMITIR ALERTA AHORA", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Botones de Llamada Directa a Emergencias (133, 131, 132)
                    Text("Llamado directo a Servicios de Emergencia:", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(6.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:133"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1565C0)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("133 Carab.", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:131"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFC62828)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("131 Ambul.", fontSize = 10.sp)
                        }
                        Spacer(modifier = Modifier.width(4.dp))
                        Button(
                            onClick = {
                                val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:132"))
                                context.startActivity(intent)
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFEF6C00)),
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(8.dp)
                        ) {
                            Icon(Icons.Default.Call, contentDescription = null, modifier = Modifier.size(14.dp))
                            Spacer(modifier = Modifier.width(2.dp))
                            Text("132 Bomb.", fontSize = 10.sp)
                        }
                    }
                }
            }
        }

        item {
            Text(
                text = "Historial Reciente de Emergencias:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(alertas) { alerta ->
            TarjetaAlerta(alerta = alerta, onCambiarEstado = { id, nuevoEstado ->
                viewModel.cambiarEstadoAlerta(id, nuevoEstado)
            })
        }
    }
}

// Visor de Google Maps Interactivo
@Composable
fun VisorGoogleMapsZaror(
    alertas: List<Alerta>,
    gpsActivo: Boolean,
    latActual: Double,
    lngActual: Double,
    onUbicacionSeleccionada: (String) -> Unit
) {
    val zarorLatLng = LatLng(-33.5042, -70.7185)
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(zarorLatLng, 15f)
    }

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                onMapClick = { latLng ->
                    onUbicacionSeleccionada("Población Zaror, Cerrillos (Lat: ${String.format(Locale.getDefault(), "%.4f", latLng.latitude)}, Lng: ${String.format(Locale.getDefault(), "%.4f", latLng.longitude)})")
                }
            ) {
                Marker(
                    state = MarkerState(position = zarorLatLng),
                    title = "Sede Social - Población Zaror",
                    snippet = "Los Jazmines #425, Cerrillos"
                )

                if (gpsActivo) {
                    Marker(
                        state = MarkerState(position = LatLng(latActual, lngActual)),
                        title = "Tu Ubicación GPS Exacta",
                        snippet = "Lat: ${String.format(Locale.getDefault(), "%.4f", latActual)}, Lng: ${String.format(Locale.getDefault(), "%.4f", lngActual)}"
                    )
                }

                alertas.filter { it.estado == "ACTIVA" }.forEachIndexed { index, alerta ->
                    val latOffset = -33.5042 + (index * 0.0012)
                    val lngOffset = -70.7185 + (index * 0.0012)
                    Marker(
                        state = MarkerState(position = LatLng(latOffset, lngOffset)),
                        title = "Alerta: ${alerta.tipo}",
                        snippet = alerta.ubicacion
                    )
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.95f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "🗺️ Toca el mapa para fijar ubicación",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "Google Maps • Población Zaror",
                    fontSize = 9.sp,
                    color = Color.DarkGray
                )
            }
        }
    }
}

// Vista de la Directiva
@Composable
fun SeccionDirectiva(viewModel: AlertaViewModel, alertas: List<Alerta>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiarRol("Encargado") }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Panel de la Directiva Zaror (Toca para ver Mantención)",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total de incidentes reportados: ${alertas.size}", fontSize = 13.sp)
                    Text(
                        text = "Alertas activas en monitoreo: ${alertas.count { it.estado == "ACTIVA" }}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }

        item {
            Text(
                text = "Historial de Incidentes Creados desde 0:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (alertas.isEmpty()) {
            item {
                Text(text = "Aún no hay alertas emitidas. Crea una desde la vista de Vecino/a.", fontSize = 13.sp, color = Color.Gray)
            }
        } else {
            items(alertas) { alerta ->
                TarjetaAlerta(alerta = alerta, onCambiarEstado = { id, nuevoEstado ->
                    viewModel.cambiarEstadoAlerta(id, nuevoEstado)
                })
            }
        }
    }
}

// Vista del Encargado (Con Switch interactivo que se activa con incidencias y botón en tarjeta)
@Composable
fun SeccionEncargado(viewModel: AlertaViewModel, fallas: List<FallaSistema>, alertas: List<Alerta>) {
    val hayEmergenciasActivas = alertas.any { it.estado == "ACTIVA" }
    var atendiendoEmergencia by remember { mutableStateOf(false) }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiarRol("Directiva") }
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Panel del Encargado (Mantención)",
                                fontWeight = FontWeight.Bold,
                                fontSize = 17.sp,
                                color = MaterialTheme.colorScheme.onSecondaryContainer
                            )
                            Text(
                                text = if (atendiendoEmergencia) "🚨 Estado: En camino a asistir incidencia" else "🟢 Estado: Disponible en central",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (atendiendoEmergencia) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                            )
                        }
                        Switch(
                            checked = atendiendoEmergencia || hayEmergenciasActivas,
                            onCheckedChange = { atendiendoEmergencia = it }
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (hayEmergenciasActivas) "🚨 ¡ATENCIÓN: Botón activado! Hay emergencias en curso." else "✅ Sistema de alarmas operativo.",
                        fontSize = 11.sp,
                        fontWeight = FontWeight.Bold,
                        color = if (hayEmergenciasActivas) MaterialTheme.colorScheme.error else Color(0xFF2E7D32)
                    )
                }
            }
        }

        item {
            Text(
                text = "Historial de Incidencias en Curso:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        if (alertas.isEmpty()) {
            item {
                Text(text = "No hay incidencias reportadas (Iniciado desde 0).", fontSize = 13.sp, color = Color.Gray)
            }
        } else {
            items(alertas) { alerta ->
                TarjetaAlerta(alerta = alerta, onCambiarEstado = { id, nuevoEstado ->
                    viewModel.cambiarEstadoAlerta(id, nuevoEstado)
                })
            }
        }

        item {
            Text(
                text = "Estado de Componentes y Sirenas:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 8.dp)
            )
        }

        items(fallas) { falla ->
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.cambiarRol("Directiva") }
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Componente: ${falla.componente}", fontWeight = FontWeight.Bold, fontSize = 14.sp, color = MaterialTheme.colorScheme.primary)
                    Text(text = "Falla: ${falla.descripcion}", fontSize = 13.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Estado: ${falla.estado}", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

// Tarjeta Individual de Alerta (Con botón de acción de estado abajo acorde al tamaño)
@Composable
fun TarjetaAlerta(alerta: Alerta, onCambiarEstado: ((Int, String) -> Unit)? = null) {
    var expandido by remember { mutableStateOf(false) }

    val colorEstado by animateColorAsState(
        targetValue = when (alerta.estado) {
            "ACTIVA" -> MaterialTheme.colorScheme.error
            "EN PROCESO" -> Color(0xFFFF9800)
            else -> Color(0xFF2E7D32)
        },
        animationSpec = tween(500),
        label = "colorAnim"
    )

    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(14.dp),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(animationSpec = tween(300))
            .clickable { expandido = !expandido }
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = alerta.tipo, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp)
                Box(
                    modifier = Modifier
                        .background(colorEstado, shape = RoundedCornerShape(8.dp))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = alerta.estado,
                        color = Color.White,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(16.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = alerta.ubicacion, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = alerta.descripcion, fontSize = 12.sp, color = Color.DarkGray)

            // Evidencia Fotografica Capturada
            alerta.fotoBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = "Evidencia Fotográfica Capturada:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(4.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(110.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                )
            }

            // Botón de acción abajo en la tarjeta (Acorde al tamaño)
            if (alerta.estado == "ACTIVA" && onCambiarEstado != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onCambiarEstado(alerta.id, "EN PROCESO") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFFF9800)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("🚨 En camino a asistir / En Proceso", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            } else if (alerta.estado == "EN PROCESO" && onCambiarEstado != null) {
                Spacer(modifier = Modifier.height(8.dp))
                Button(
                    onClick = { onCambiarEstado(alerta.id, "RESUELTA") },
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32)),
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(38.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("✅ Marcar como Resuelta / Cerrada", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = Color.White)
                }
            }

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Registrado: ${alerta.fechaHora}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
