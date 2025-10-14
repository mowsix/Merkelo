# 🛒 MerqueloApp

Aplicación móvil desarrollada en **Kotlin con Jetpack Compose**, que permite a los usuarios **crear listas de mercado personalizadas**, gestionar **tiendas favoritas**, **agregar productos**, y visualizar **ubicaciones en el mapa de Medellín** sin necesidad de una API Key.

---

## 🌟 Funcionalidades principales

- 🧾 **Creación de listas de mercado** con múltiples tiendas y productos.
- 🏬 **Gestión de tiendas favoritas**, incluyendo sugerencias predefinidas (D1, Ara, Éxito, Carulla, Euro, etc.).
- 🗺️ **Mapa interactivo (OSMDroid)** que muestra las ubicaciones de las principales cadenas en Medellín.
- 💡 **Modo claro / oscuro**, seleccionable desde el perfil.
- 🧭 Navegación completa mediante **Navigation Compose**.
- 💾 Persistencia de datos local con **Room**.
- 📦 Arquitectura en capas (UI - ViewModel - Repository - Database).

---

## 🧱 Arquitectura

El proyecto sigue una estructura limpia, basada en **MVVM (Model-View-ViewModel)**:

## 🧱 Arquitectura

El proyecto sigue una estructura limpia, basada en **MVVM (Model-View-ViewModel)**:

```plaintext
app/
├── data/
│   ├── local/
│   │   ├── Entities.kt
│   │   └── Daos.kt
│   └── MarketRepository.kt
│
├── ui/
│   ├── components/            # TopBar, BottomBar, botones reutilizables
│   ├── screens/               # Home, CreateList, AddProduct, Stores, Profile, Map, etc.
│   └── theme/                 # Colores, tipografía, modo oscuro/claro
│
├── navigation/
│   ├── Routes.kt
│   └── AppNavigation.kt
│
├── App.kt                     # Configuración de OSMDroid
└── MainActivity.kt            # Entry point Compose
```

---

## ⚙️ Requerimientos

- **Android Studio Giraffe (o superior)**
- **Gradle 8.x**
- **Kotlin 1.9+**
- Mínimo SDK: **Android 8.0 (API 26)**
- Internet habilitado (para cargar tiles de OpenStreetMap)
- Sin necesidad de clave de Google Maps ✅

---

## 🧩 Dependencias principales

```gradle
// Jetpack Compose
implementation "androidx.compose.ui:ui:1.7.0"
implementation "androidx.compose.material3:material3:1.3.0"
implementation "androidx.navigation:navigation-compose:2.7.7"

// Room (persistencia local)
implementation "androidx.room:room-runtime:2.6.1"
kapt "androidx.room:room-compiler:2.6.1"
implementation "androidx.room:room-ktx:2.6.1"

// Coroutines
implementation "org.jetbrains.kotlinx:kotlinx-coroutines-android:1.8.1"

// OSMDroid (mapa sin API Key)
implementation "org.osmdroid:osmdroid-android:6.1.16"

// Lifecycle y ViewModel
implementation "androidx.lifecycle:lifecycle-runtime-compose:2.7.0"
implementation "androidx.lifecycle:lifecycle-viewmodel-compose:2.7.0"
