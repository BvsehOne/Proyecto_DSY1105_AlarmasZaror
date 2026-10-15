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
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.drawscope.Stroke
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
            // Seleccionar Rol (Perfil Activo)
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

    // Launcher para Permiso de Ubicación
    val launcherPermisoUbicacion = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            latitudGps = -33.5042
            longitudGps = -70.7185
            ubicacionTexto = "Los Jazmines #425, Pasaje 2 (GPS Zaror)"
            viewModel.limpiarErrores()
            Toast.makeText(context, "Ubicación GPS fijada en el mapa", Toast.LENGTH_SHORT).show()
        } else {
            Toast.makeText(context, "Permiso de GPS denegado", Toast.LENGTH_SHORT).show()
        }
    }

    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            // Mapa Vectorial Real del Sector Población Zaror
            VisorMapaZaror(alertas, latitudGps != null)
        }

        item {
            // Animacion de Confirmacion
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
                            text = "¡Alerta registrada y notificada al sector Zaror!",
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
                                text = "ACTIVAR ALARMA DE EMERGENCIA",
                                fontWeight = FontWeight.ExtraBold,
                                color = MaterialTheme.colorScheme.error,
                                fontSize = 15.sp
                            )
                            Text(
                                text = "Envío prioritario a la comunidad de Cerrillos",
                                fontSize = 11.sp,
                                color = Color.DarkGray
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Tipos de Incidente (Botones M3 Novedosos)
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        listOf("Robo / Asalto", "Incendio", "Médica", "Sospecha").forEach { tipo ->
                            val seleccionado = (tipoSeleccionado == tipo)
                            FilterChip(
                                selected = seleccionado,
                                onClick = { tipoSeleccionado = tipo },
                                label = {
                                    Text(
                                        text = tipo,
                                        fontSize = 11.sp,
                                        fontWeight = if (seleccionado) FontWeight.Bold else FontWeight.Medium
                                    )
                                }
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    // Campo 1: Ubicacion con Validación Visual
                    OutlinedTextField(
                        value = ubicacionTexto,
                        onValueChange = {
                            ubicacionTexto = it
                            if (it.length >= 4) viewModel.limpiarErrores()
                        },
                        label = { Text("Calle, Pasaje o Número aproximado") },
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

                    // Campo 2: Detalle con Validación Visual
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

                    // Botones de Cámara y GPS Modernos
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
                                    latitudGps = -33.5042
                                    longitudGps = -70.7185
                                    ubicacionTexto = "Los Jazmines #425, Pasaje 2 (GPS Zaror)"
                                    viewModel.limpiarErrores()
                                    Toast.makeText(context, "Ubicación GPS obtenida", Toast.LENGTH_SHORT).show()
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

                    // Previsualizacion de Foto de Evidencia
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
            TarjetaAlerta(alerta = alerta)
        }
    }
}

// Visor de Mapa Vectorial Realista de la Población Zaror (Cerrillos, Santiago)
@Composable
fun VisorMapaZaror(alertas: List<Alerta>, gpsActivo: Boolean) {
    ElevatedCard(
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
        shape = RoundedCornerShape(16.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(180.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFE2ECE9)) // Color mapa base plano urbano
        ) {
            Canvas(modifier = Modifier.fillMaxSize()) {
                val w = size.width
                val h = size.height

                // 1. Manzanas / Viviendas (Bloques de la Población Zaror - 12 Cuadras)
                val blockColor = Color(0xFFC8E6C9)
                val strokeColor = Color(0xFFA5D6A7)

                // Fila Superior de Cuadras
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.05f, h * 0.12f), Size(w * 0.25f, h * 0.32f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.37f, h * 0.12f), Size(w * 0.26f, h * 0.32f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.70f, h * 0.12f), Size(w * 0.25f, h * 0.32f), CornerRadius(8f, 8f))

                // Fila Inferior de Cuadras
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.05f, h * 0.55f), Size(w * 0.25f, h * 0.28f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.37f, h * 0.55f), Size(w * 0.26f, h * 0.28f), CornerRadius(8f, 8f))
                drawRoundRect(Color(0xFFDCEDC8), Offset(w * 0.70f, h * 0.55f), Size(w * 0.25f, h * 0.28f), CornerRadius(8f, 8f))

                // 2. Calles / Avenidas Principales del Sector Zaror
                val roadColor = Color.White
                val roadLineColor = Color(0xFFBDBDBD)

                // Eje Central: Av. Los Jazmines (Horizontal principal)
                drawRect(roadColor, Offset(0f, h * 0.46f), Size(w, h * 0.08f))
                drawLine(roadLineColor, Offset(0f, h * 0.46f), Offset(w, h * 0.46f), strokeWidth = 2f)
                drawLine(roadLineColor, Offset(0f, h * 0.54f), Offset(w, h * 0.54f), strokeWidth = 2f)

                // Pasaje Las Rosas (Vertical 1)
                drawRect(roadColor, Offset(w * 0.31f, 0f), Size(w * 0.05f, h))
                // Pasaje Los Tulipanes (Vertical 2)
                drawRect(roadColor, Offset(w * 0.64f, 0f), Size(w * 0.05f, h))

                // Av. Pedro Aguirre Cerda (Borde inferior)
                drawRect(Color(0xFFFFF9C4), Offset(0f, h * 0.86f), Size(w, h * 0.14f))
                drawLine(Color(0xFFFBC02D), Offset(0f, h * 0.86f), Offset(w, h * 0.86f), strokeWidth = 3f)

                // 3. Marcador de Sede Social (Los Jazmines #425)
                val sedeX = w * 0.50f
                val sedeY = h * 0.50f
                drawCircle(Color(0xFF1976D2), radius = 12f, center = Offset(sedeX, sedeY))
                drawCircle(Color.White, radius = 5f, center = Offset(sedeX, sedeY))

                // 4. Marcador de Sirena de Alarma Comunitaria
                drawCircle(Color(0xFF388E3C), radius = 9f, center = Offset(w * 0.18f, h * 0.28f))

                // 5. Marcadores de Alertas Activas de Emergencia (Puntos Rojos destacados)
                alertas.filter { it.estado == "ACTIVA" }.forEachIndexed { index, _ ->
                    val posX = w * (0.18f + (index * 0.32f) % 0.70f)
                    val posY = h * (0.28f + (index * 0.28f) % 0.50f)
                    // Pulso exterior
                    drawCircle(Color.Red.copy(alpha = 0.3f), radius = 22f, center = Offset(posX, posY))
                    drawCircle(Color.Red, radius = 12f, center = Offset(posX, posY))
                    drawCircle(Color.White, radius = 5f, center = Offset(posX, posY))
                }

                // 6. Marcador GPS del Usuario si está activo
                if (gpsActivo) {
                    val gpsX = w * 0.48f
                    val gpsY = h * 0.42f
                    drawCircle(Color(0xFF00C853).copy(alpha = 0.35f), radius = 26f, center = Offset(gpsX, gpsY))
                    drawCircle(Color(0xFF00C853), radius = 11f, center = Offset(gpsX, gpsY))
                    drawCircle(Color.White, radius = 4f, center = Offset(gpsX, gpsY))
                }
            }

            // Etiquetas e Información del Mapa
            Column(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopStart)
                    .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = "Mapa Sector Zaror • Cerrillos",
                    fontWeight = FontWeight.ExtraBold,
                    fontSize = 11.sp,
                    color = Color(0xFF1A237E)
                )
                Text(
                    text = "Eje: Av. Los Jazmines con Las Rosas",
                    fontSize = 9.sp,
                    color = Color.DarkGray
                )
            }

            // Indicador del Norte (Brújula)
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.TopEnd)
                    .background(Color.White.copy(alpha = 0.92f), CircleShape)
                    .padding(6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.LocationOn,
                    contentDescription = "Norte",
                    tint = Color(0xFF1A237E),
                    modifier = Modifier.size(16.dp)
                )
                Text("N", fontSize = 10.sp, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
            }

            // Leyenda de Simbología del Mapa
            Row(
                modifier = Modifier
                    .padding(8.dp)
                    .align(Alignment.BottomStart)
                    .background(Color.White.copy(alpha = 0.92f), RoundedCornerShape(8.dp))
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color(0xFF1976D2), CircleShape))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Sede Social", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(8.dp).background(Color.Red, CircleShape))
                    Spacer(modifier = Modifier.width(3.dp))
                    Text("Emergencia", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color.Red)
                }

                if (gpsActivo) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(modifier = Modifier.size(8.dp).background(Color(0xFF00C853), CircleShape))
                        Spacer(modifier = Modifier.width(3.dp))
                        Text("Tu GPS", fontSize = 9.sp, fontWeight = FontWeight.Bold, color = Color(0xFF00C853))
                    }
                }
            }
        }
    }
}

// Vista de la Directiva
@Composable
fun SeccionDirectiva(alertas: List<Alerta>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Panel de la Directiva Zaror",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Total de emergencias registradas: ${alertas.size}",
                        fontSize = 13.sp,
                        color = MaterialTheme.colorScheme.onPrimaryContainer
                    )
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

// Vista del Encargado
@Composable
fun SeccionEncargado(fallas: List<FallaSistema>) {
    LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        item {
            ElevatedCard(
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Mantenimiento del Sistema de Alarmas",
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                    Text(
                        text = "Estado de sirenas, baterías y hardware del sector.",
                        fontSize = 12.sp,
                        color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.8f)
                    )
                }
            }
        }

        items(fallas) { falla ->
            ElevatedCard(
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text(text = "Componente: ${falla.componente}", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text(text = "Falla: ${falla.descripcion}", fontSize = 13.sp, color = Color.DarkGray)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(text = "Estado: ${falla.estado}", fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                }
            }
        }
    }
}

// Tarjeta Individual de Alerta
@Composable
fun TarjetaAlerta(alerta: Alerta) {
    var expandido by remember { mutableStateOf(false) }

    val colorEstado by animateColorAsState(
        targetValue = when (alerta.estado) {
            "ACTIVA" -> MaterialTheme.colorScheme.error
            "EN ATENCIÓN" -> Color(0xFFFF9800)
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
                        .background(colorEstado, shape = RoundedCornerShape(12.dp))
                        .padding(horizontal = 10.dp, vertical = 4.dp)
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

            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "Registrado: ${alerta.fechaHora}",
                fontSize = 10.sp,
                color = MaterialTheme.colorScheme.outline
            )
        }
    }
}
