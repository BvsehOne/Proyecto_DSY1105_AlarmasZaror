package com.AlarmasZaror.api

import io.ktor.server.application.*
import io.ktor.server.response.*
import io.ktor.server.routing.*
import io.ktor.server.engine.*
import io.ktor.server.netty.*

fun main() {
    embeddedServer(Netty, port = 8080) {
        routing {
            get("/api/hola") {
                call.respondText("¡Hola desde mi API en el repositorio de Android!")
            }
        }
    }.start(wait = true)
}