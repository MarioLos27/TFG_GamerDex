# 🎮 GamerDex

**GamerDex** es una aplicación Android nativa diseñada para la búsqueda y visualización de videojuegos, desarrollada como Trabajo de Fin de Grado (TFG). Utiliza la API de [IGDB (Internet Game Database)](https://www.igdb.com/) para obtener información actualizada sobre los juegos.

El proyecto está construido siguiendo los principios de **Clean Architecture** y patrones modernos de desarrollo en Android.

## 📦 Tecnologías y Arquitectura

- **Lenguaje:** Kotlin 2.0+
- **Interfaz de Usuario:** Jetpack Compose
- **Arquitectura:** Clean Architecture + MVVM (Model-View-ViewModel)
- **Inyección de Dependencias:** Hilt
- **Red y Datos:** Retrofit (Cliente HTTP) y Coil (Carga de imágenes)
- **Estado Reactivo:** StateFlow
- **API:** Integración con IGDB API v4

## 🚀 Cómo empezar (Quickstart)

Para ejecutar la aplicación localmente, necesitas configurar las credenciales de la API de IGDB:

1. **Obtener credenciales IGDB:**
   - Necesitas una cuenta de [Twitch Developer](https://dev.twitch.tv/console/apps) para obtener un `Client ID` y un `Access Token` para la API de IGDB.

2. **Configurar el proyecto:**
   - Copia el archivo `local.properties.example` y renómbralo a `local.properties` en la raíz del proyecto.
   - Edita `local.properties` y añade tus credenciales en `igdb.client.id` y `igdb.access.token`.
   *(Nota: `local.properties` está ignorado en Git para proteger tus credenciales).*

3. **Compilar y Ejecutar:**
   - Abre el proyecto en Android Studio.
   - Sincroniza el proyecto con Gradle.
   - Selecciona un emulador o dispositivo físico y ejecuta la aplicación (Run 'app').

## 👨‍💻 Desarrollador

**Mario**  
Proyecto académico - Trabajo de Fin de Grado (TFG)
