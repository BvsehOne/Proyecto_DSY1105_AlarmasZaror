# 🧠 Cuestionario Interactivo y Explicación Técnica Total del Proyecto
**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles (Duoc UC)
**Proyecto:** App de Gestión de Alarmas Comunitarias - Población Zaror
**Grupo 2 (Defensa Hoy 19:15 hrs):** Nicolás Aceitón, Jordan Olivares, Angelo Olivares, Marcelo Pincheira

---

## 🧭 ¿Cómo usar este cuestionario?
Este documento cubre **absolutamente todo el código y funcionamiento** de la aplicación móvil que construimos en Android Studio. Repasen estas preguntas y respuestas con su compañero para dominar cada archivo, línea y decisión técnica durante la defensa de 10 minutos.

---

## 📱 MÓDULO 1: Interfaz de Usuario y Jetpack Compose (`MainActivity.kt`)

### Q1: ¿Qué es Jetpack Compose y por qué no usamos XML?
- **Respuesta Técnica:** Jetpack Compose es el kit de herramientas moderno de Google para crear interfaces nativas en Android de forma **declarativa**. En lugar de diseñar archivos XML y buscarlos con `findViewById`, escribimos funciones en Kotlin anotadas con `@Composable`. Cuando el estado cambia, Compose actualiza la pantalla automáticamente mediante *recomposición*.

### Q2: ¿Cómo está estructurada la pantalla principal en `MainActivity.kt`?
- **Respuesta Técnica:**
  1. **`PantallaPrincipalZaror`:** Contiene el `Scaffold` con la `TopAppBar` superior (título "Alarmas Zaror - 425 Viviendas") y un selector de roles (`FilterChip` / barras de pestañas) para cambiar dinámicamente entre **Vecino/a**, **Directiva** y **Encargado**.
  2. **`SeccionVecino`:** Contiene el mapa interactivo de Google Maps, el formulario de pánico con validaciones en tiempo real, botones de Cámara y GPS, y la lista de alertas (`LazyColumn`).
  3. **`VisorGoogleMapsZaror`:** Renderiza el mapa oficial de Google Maps con marcadores para la Sede Social, el GPS del usuario y las alertas activas.
  4. **`TarjetaAlerta`:** Muestra cada incidente con un chip de estado animado (`animateColorAsState`) y previsualización de la foto de evidencia capturada.

---

## 🧠 MÓDULO 2: Arquitectura MVVM y Estados (`AlertaViewModel.kt`)

### Q3: ¿Cómo funciona el patrón MVVM en este proyecto y qué hace cada capa?
- **Respuesta Técnica:**
  - **Model (`Alerta.kt`):** Es la clase de datos (`data class`) que define los atributos de la alerta (tipo, ubicación, descripción, bitmap de foto, lat/lng).
  - **View (`MainActivity.kt`):** Funciones `@Composable` que renderizan la UI y capturan interacciones del usuario.
  - **ViewModel (`AlertaViewModel.kt`):** Gestiona los datos y la lógica de negocio. Mantiene las alertas en un `LiveData` (`_alertas`) y sobrevive a los cambios de configuración (como girar la pantalla).
  - **Repository (`RepositorioAlarma.kt`):** Fuente única de verdad que provee las listas iniciales y agrega nuevas alertas.

### Q4: ¿Cómo se actualiza la interfaz cuando se emite una nueva alerta?
- **Respuesta Técnica:** Usamos programación reactiva con **`LiveData`** y **`observeAsState()`**. En `MainActivity`, la línea `val listaAlertas by viewModel.alertas.observeAsState()` convierte el LiveData en un estado de Compose. Cuando el usuario presiona el botón de pánico, el ViewModel agrega la alerta a la lista y actualiza el LiveData, haciendo que Compose redibuje la lista al instante.

---

## 🗺️ MÓDULO 3: Integración de Google Maps API

### Q5: ¿Cómo funciona el visor de Google Maps en la aplicación?
- **Respuesta Técnica:** Utilizamos la librería oficial `com.google.maps.android.compose.GoogleMap`. El componente recibe el estado de la cámara (`rememberCameraPositionState`) centrado en las coordenadas reales de la Población Zaror en Cerrillos (`-33.5042, -70.7185`). Además, renderiza componentes `Marker` interactivos para la Sede Social, la ubicación exacta del GPS del usuario y los incidentes activos.

---

## ✅ MÓDULO 4: Formularios y Validación Desacoplada

### Q6: ¿Cómo se validan los campos del formulario de pánico?
- **Respuesta Técnica:** La validación está **desacoplada de la UI**. Al hacer clic en "Emitir Alerta", la vista llama a `viewModel.validarFormulario(ubicacion, descripcion)`. Si los campos son inválidos, el ViewModel asigna textos de error a `errorUbicacion` y `errorDescripcion`. En la UI, el `OutlinedTextField` lee estas variables mediante `isError = true` y muestra el mensaje de advertencia en el `supportingText`.

---

## 📷 MÓDULO 5: Recursos Nativos (Cámara y GPS)

### Q7: ¿Cómo se integra la Cámara y el GPS de forma segura?
- **Respuesta Técnica:**
  - **Cámara:** Usamos `ActivityResultContracts.TakePicturePreview()`. Al presionar el botón "Foto Evidencia", comprobamos el permiso `Manifest.permission.CAMERA` y abrimos la cámara nativa. La foto obtenida se almacena como un `Bitmap` en el estado y se renderiza con bordes redondeados (`asImageBitmap()`).
  - **GPS:** Usamos permisos dinámicos en tiempo de ejecución (`ACCESS_FINE_LOCATION`) para obtener la ubicación exacta del usuario y fijarla automáticamente en el formulario y en el mapa de Google Maps.

---

## 💾 MÓDULO 6: Persistencia Local (Room) y Remota (Retrofit)

### Q8: ¿Cómo funciona Room Database en el proyecto?
- **Respuesta Técnica:** Room es la librería oficial de persistencia de Android sobre SQLite. Consta de 3 partes:
  1. **`AlertaEntity` (`@Entity`):** Define la tabla en SQLite.
  2. **`AlertaDao` (`@Dao`):** Interfaz con las consultas `@Query` y `@Insert`.
  3. **`AppDatabase` (`@Database`):** Clase abstracta que une la entidad y expone el DAO.

### Q9: ¿Cómo funciona Retrofit con el Microservicio?
- **Respuesta Técnica:** `ApiService.kt` define las funciones suspendidas (`@GET`, `@POST`) y `RetrofitClient.kt` configura la URL base (`http://10.0.2.2:8080/`) con el conversor de Gson para intercambiar datos JSON con el backend en Spring Boot.

---

## 🧪 MÓDULO 7: Pruebas Unitarias (Testing - Guía 15)

### Q10: ¿Qué prueban las pruebas unitarias y cómo se ejecutan?
- **Respuesta Técnica:** En `AlertaViewModelTest.kt`, probamos que la función `validarFormulario()` retorne `false` con campos vacíos y `true` con datos correctos. Utilizamos `InstantTaskExecutorRule` para permitir que el `LiveData` corra de forma síncrona en las pruebas. Se ejecutan con `gradle_build("app:testDebugUnitTest")` o haciendo clic en el icono de Play al lado de la clase en Android Studio.
