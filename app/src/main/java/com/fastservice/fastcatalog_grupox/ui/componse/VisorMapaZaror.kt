package com.fastservice.fastcatalog_grupox.ui.componse

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.fastservice.fastcatalog_grupox.modelo.Alerta

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
