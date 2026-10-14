plugins {
    kotlin("jvm")
    application
}

group = "com.AlarmasZaror.api"
version = "1.0"

application {
    mainClass.set("com.AlarmasZaror.api.ApplicationKt")
}

kotlin {
    jvmToolchain(17) // puede 21 dependiendo de tu JDK configurado
}

dependencies {
    implementation("io.ktor:ktor-server-core-jvm:2.3.7")
    implementation("io.ktor:ktor-server-netty-jvm:2.3.7")
    implementation("ch.qos.logback:logback-classic:1.4.14")
}