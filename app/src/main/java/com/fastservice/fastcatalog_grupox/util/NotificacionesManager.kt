package com.fastservice.fastcatalog_grupox.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build

// Manejo de notificaciones de emergencia para el sector Zaror
class NotificacionesManager(private val context: Context) {

    fun crearCanalAlerta() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val canal = NotificationChannel(
                "CANAL_ALERTAS_ZAROR",
                "Alertas Urgentes Poblacion Zaror",
                NotificationManager.IMPORTANCE_HIGH
            )
            val manager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(canal)
        }
    }
}
