package com.fastservice.fastcatalog_grupox.ui.componse

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import com.google.android.gms.location.LocationServices

@SuppressLint("MissingPermission")
fun obtenerUbicacionReal(
    context: Context,
    onSuccess: (Double, Double) -> Unit
) {
    val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context)

    fusedLocationClient.lastLocation
        .addOnSuccessListener { location ->
            if (location != null) {
                onSuccess(location.latitude, location.longitude)
            } else {
                Toast.makeText(context, "Activa el GPS en los ajustes de tu teléfono", Toast.LENGTH_LONG).show()
            }
        }
        .addOnFailureListener {
            Toast.makeText(context, "Error al obtener señal del GPS", Toast.LENGTH_SHORT).show()
        }
}