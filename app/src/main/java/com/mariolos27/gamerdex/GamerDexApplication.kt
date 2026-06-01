package com.mariolos27.gamerdex

import android.app.Application
import dagger.hilt.android.HiltAndroidApp

/**
 * Clase Application para inicializar Hilt.
 *
 * Esta clase debe estar decorada con @HiltAndroidApp para que Hilt
 * configure el contenedor de inyección de dependencias.
 */
@HiltAndroidApp
class GamerDexApplication : Application()

