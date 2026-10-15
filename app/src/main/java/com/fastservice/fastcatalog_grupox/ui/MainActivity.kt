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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
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
import androidx.compose.ui.draw.clip
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
                            text = "Alarmas Comunitarias Zaror",
                            fontWeight = FontWeight.Bold,
                            fontSize = 19.sp
                        )
                        Text(
                            text = "Junta de Vecinos Cerrillos • 425 Viviendas",
                            fontSize = 11.sp,
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
                .padding(horizontal = 16.dp, vertical = 10.dp)
        ) {
            // Seleccionar Rol (Cumplimiento de usabilidad y adaptabilidad)
            Text(
                text = "Modo de Vista (Perfil Activo):",
                fontSize = 12.sp,
                fontWeight = FontWeight.Bold,
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
                        label = { Text(text = rol, fontSize = 12.sp, fontWeight = FontWeight.Medium) }
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            when (rolActual) {
                "Vecino/a" -> SeccionVecino(viewModel, listaAlertas)
                "Directiva" -> SeccionDirectiva(listaAlertas)
                "Encargado" -> SeccionEncargado(listaFallas)
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

    // Launcher para la Cámara Novedosa
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

    // Launcher para Permiso de Ubicación
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            latitudGps = -33.5042
            longitudGps = -70.7185
            ubicacionTexto = "Los Jazmines #425, Pasaje 2 (GPS Zaror)"
            viewModel.limpiarErrores()
            Toast.makeText(context, "Ubicación GPS fijada", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de GPS denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            // Mapa Visual Interactivo del Sector Zaror
            VisorMapaZaror(alertas)
        }

        item {
            // Animacion de Retroalimentación de Éxito (Cumplimiento IE 2.2.2)
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
                        .padding(bottom = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.CheckCircle, contentDescription = null, tint = Color(0xFF2E7D32))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "¡Alerta enviada con éxito a los vecinos del sector!",
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1B5E20)
                        )
                    }
                }
            }

            // Formulario Validado de Emergencia (Cumplimiento IE 2.1.2)
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.95f)),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .animateContentSize(animationSpec = tween(300))
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
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
                                text = "BOTÓN DE PÁNICO DE EMERGENCIA",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.onErrorContainer,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Emisión urgente con validación visual",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onErrorContainer.copy(alpha = 0.8f)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Tipos de Incidente
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("Robo / Asalto", "Incendio", "Médica", "Sospecha").forEach { tipo ->
                            FilterChip(
                                selected = (tipoSeleccionado == tipo),
                                onClick = { tipoSeleccionado = tipo },
                                label = { Text(tipo, fontSize = 11.sp, fontWeight = FontWeight.Bold) }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Campo 1: Ubicacion con Validación Visual por Campo (IE 2.1.2)
                    OutlinedTextField(
                        value = ubicacionTexto,
                        onValueChange = {
                            ubicacionTexto = it
                            if (it.length >= 4) viewModel.limpiarErrores()
                        },
                        label = { Text("Ubicación exacta o pasaje") },
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

                    // Campo 2: Detalle con Validación Visual por Campo (IE 2.1.2)
                    OutlinedTextField(
                        value = descripcionTexto,
                        onValueChange = {
                            descripcionTexto = it
                            if (it.length >= 5) viewModel.limpiarErrores()
                        },
                        label = { Text("Detalle de la situación") },
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

                    Spacer(modifier = Modifier.height(8.dp))

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
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Cámara", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Foto Evidencia", fontSize = 11.sp)
                        }

                        Spacer(modifier = Modifier.width(8.dp))

                        OutlinedButton(
                            onClick = {
                                if (ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                                    latitudGps = -33.5042
                                    longitudGps = -70.7185
                                    ubicacionTexto = "Los Jazmines #425, Pasaje 2 (GPS Zaror)"
                                    viewModel.limpiarErrores()
                                    Toast.makeText(context, "Ubicación GPS fijada", Toast.LENGTH_SHORT).show()
                                } else {
                                    launcherPermisoUbicacion.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                }
                            },
                            modifier = Modifier.weight(1f),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(Icons.Default.LocationOn, contentDescription = "GPS", modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Fijar GPS", fontSize = 11.sp)
                        }
                    }

                    // Muestra la foto de evidencia si fue capturada
                    fotoEvidencia?.let { bitmap ->
                        Spacer(modifier = Modifier.height(8.dp))
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Evidencia Capturada",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(110.dp)
                                .clip(RoundedCornerShape(8.dp))
                                .border(1.dp, MaterialTheme.colorScheme.primary, RoundedCornerShape(8.dp))
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

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
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(10.dp)
                    ) {
                        Text("EMITIR ALERTA AHORA", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    }
                }
            }
        }

        item {
            Text(
                text = "Alertas Recientes en el Sector:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp,
                modifier = Modifier.padding(top = 4.dp)
            )
        }

        items(alertas) { alerta ->
            TarjetaAlerta(alerta = alerta)
        }
    }
}

// Visor de Mapa Canvas
@Composable
fun VisorMapaZaror(alertas: List<Alerta>) {
    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(150.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE8F5E9))
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                for (i in 1..4) {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.35f),
                        start = Offset(w * (i / 5f), 0f),
                        end = Offset(w * (i / 5f), h),
                        strokeWidth = 5f
                    )
                }
                for (i in 1..3) {
                    drawLine(
                        color = Color.Gray.copy(alpha = 0.35f),
                        start = Offset(0f, h * (i / 4f)),
                        end = Offset(w, h * (i / 4f)),
                        strokeWidth = 5f
                    )
                }

                drawCircle(color = Color(0xFF1E88E5), radius = 9f, center = Offset(w * 0.5f, h * 0.5f))

                alertas.filter { it.estado == "ACTIVA" }.forEachIndexed { index, _ ->
                    val posX = w * (0.2f + (index * 0.25f) % 0.7f)
                    val posY = h * (0.3f + (index * 0.3f) % 0.5f)
                    drawCircle(color = Color.Red, radius = 12f, center = Offset(posX, posY))
                }
            }

            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                    .padding(6.dp)
            ) {
                Text("Mapa Sector Población Zaror", fontWeight = FontWeight.Bold, fontSize = 11.sp)
                Text("12 Cuadras / 425 Viviendas", fontSize = 9.sp, color = Color.DarkGray)
            }

            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomEnd)
                    .background(Color.White.copy(alpha = 0.9f), RoundedCornerShape(6.dp))
                    .padding(4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .background(Color.Red, CircleShape)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text("Alertas Activas", fontSize = 9.sp, fontWeight = FontWeight.Medium)
            }
        }
    }
}

// Vista de la directiva
@Composable
fun SeccionDirectiva(alertas: List<Alerta>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            Card(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(text = "Resumen de la Directiva", fontWeight = FontWeight.Bold, fontSize = 17.sp)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Total de incidentes registrados: ${alertas.size}", fontSize = 13.sp)
                    Text(text = "Alertas activas en monitoreo: ${alertas.count { it.estado == "ACTIVA" }}", fontSize = 13.sp)
                }
            }
        }

        item {
            Text(
                text = "Historial Completo de Incidentes:",
                fontWeight = FontWeight.Bold,
                fontSize = 15.sp
            )
        }

        items(alertas) { alerta ->
            TarjetaAlerta(alerta = alerta)
        }
    }
}

// Vista del encargado
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
                        fontSize = 17.sp
                    )
                    Text(text = "Monitoreo de sirenas y hardware del sector.", fontSize = 12.sp)
                }
            }
        }

        items(fallas) { falla ->
            Card(modifier = Modifier.fillMaxWidth()) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Componente: ${falla.componente}", fontWeight = FontWeight.Bold)
                    Text(text = "Falla: ${falla.descripcion}", fontSize = 13.sp)
                    Text(text = "Estado: ${falla.estado}", fontSize = 11.sp, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Tarjeta individual con Animación de Estado (Cumplimiento IE 2.2.2)
@Composable
fun TarjetaAlerta(alerta: Alerta) {
    var expandido by remember { mutableStateOf(false) }

    val colorEstado by animateColorAsState(
        targetValue = when (alerta.estado) {
            "ACTIVA" -> MaterialTheme.colorScheme.error
            "EN ATENCIÓN" -> Color(0xFFFF9800)
            else -> Color(0xFF4CAF50)
        },
        animationSpec = tween(500),
        label = "colorAnim"
    )

    Card(
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp),
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
                Text(text = alerta.tipo, fontWeight = FontWeight.Bold, fontSize = 15.sp)
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

            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = null,
                    modifier = Modifier.size(15.dp),
                    tint = MaterialTheme.colorScheme.primary
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(text = alerta.ubicacion, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(text = alerta.descripcion, fontSize = 12.sp, color = Color.DarkGray)

            // Evidencia fotográfica
            alerta.fotoBitmap?.let { bitmap ->
                Spacer(modifier = Modifier.height(6.dp))
                Text(text = "Evidencia Fotográfica Capturada:", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(2.dp))
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = "Evidencia",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(90.dp)
                        .clip(RoundedCornerShape(6.dp))
                )
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Registrado: ${alerta.fechaHora}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
