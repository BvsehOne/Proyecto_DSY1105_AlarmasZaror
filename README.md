# 🚨 App de Gestión de Alarmas Comunitarias - Población Zaror
**Asignatura:** DSY1105 - Desarrollo de Aplicaciones Móviles (Duoc UC)  
**Evaluación:** Evaluación Parcial 2 - Encargo  
**Integrantes:**
- Angelo Olivares (Tech Lead & UI Architect)
- Marcelo Pincheira (Mobile Data & Native Features Specialist)
- Nicolas Aceiton (Backend & API Integration Architect)
- Jordan Olivares (QA, Security & Admin Manager)

---

## 📌 Descripción del Proyecto
Aplicación móvil desarrollada en **Android (Kotlin)** con **Jetpack Compose** y **Material Design 3** para fortalecer la seguridad de la Junta de Vecinos de la **Población Zaror (Cerrillos)** (12 cuadras, 425 viviendas). La solución permite la activación rápida de alarmas comunitarias, geolocalización GPS, evidencia fotográfica, visor de mapa del sector y monitoreo por parte de la directiva y encargados de mantenimiento.

---

## 🚀 Requisitos y Funcionalidades Implementadas (Rúbrica 100%)

### 1. Interfaz Móvil y Navegación Coherente (IE 2.1.1)
- Diseño moderno con componentes Material 3 (`ElevatedCard`, `FilterChip`, `TopAppBar`).
- Cambio dinámico de vista según el perfil de usuario (**Vecino/a**, **Directiva**, **Encargado de Mantenimiento**).
- Visor de mapa en `Canvas` que simula la cuadrícula de las 12 cuadras de la Población Zaror con marcadores de emergencias activas.

### 2. Formularios Validados e Íconos Visuales (IE 2.1.2)
- Formulario de activación de alarma con validación por campo en tiempo real.
- Mensajes de error específicos e íconos de advertencia (`isError`, `supportingText`).

### 3. Lógica Desacoplada y Gestión de Estado MVVM (IE 2.2.1)
- Arquitectura **MVVM (Model-View-ViewModel)**.
- Validación centralizada en `AlertaViewModel` desacoplada de la UI de Compose.

### 4. Animaciones Visuales Funcionales (IE 2.2.2)
- `AnimatedVisibility` para banner de confirmación de envío.
- `animateContentSize()` para expansiones suaves de tarjetas de formulario.
- `animateColorAsState` para transiciones suaves de color según el estado del incidente (*ACTIVA*, *EN ATENCIÓN*, *RESUELTA*).

### 5. Estructura Modular y Persistencia Local Room (IE 2.3.1)
- Organización limpia de paquetes:
  - `data/local/`: `AlertaEntity`, `AlertaDao`, `AppDatabase` (Persistencia en SQLite con Room).
  - `modelo/`: `Alerta.kt`, `FallaSistema.kt`.
  - `viewmodel/`: `AlertaViewModel.kt`.
  - `ui/`: `MainActivity.kt`, `theme/`.

### 6. Acceso a Recursos Nativos (IE 2.4.1)
- **Cámara:** Captura de evidencia fotográfica adjunta al reporte de emergencia (`TakePicturePreview`).
- **GPS / Ubicación:** Geolocalización del incidente mediante `FusedLocationProviderClient` y permisos dinámicos.

---

## 🛠️ Instrucciones para Compilar y Ejecutar

1. Clonar el repositorio:
   ```bash
   git clone https://github.com/BvsehOne/Proyecto_DSY1105_AlarmasZaror.git
   ```
2. Abrir la carpeta del proyecto en **Android Studio**.
3. Sincronizar las dependencias de Gradle.
4. Seleccionar la configuración de inicio **`app`** y presionar **Run ▶️** (`Shift + F10`).
