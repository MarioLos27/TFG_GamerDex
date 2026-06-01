package com.mariolos27.gamerdex.presentation.navigation

/**
 * Definición de las rutas de navegación de la aplicación.
 *
 * Estructura:
 * - Auth - Pantallas sin autenticación
 * - App - Pantallas autenticadas
 */
sealed class Screen(val route: String) {
    // Pantallas de Autenticación
    data object Login : Screen("login_screen")
    data object Register : Screen("register_screen")
    data object Username : Screen("username_screen")

    // Pantallas de la Aplicación
    data object Dashboard : Screen("dashboard_screen")
    data object Library : Screen("library_screen")
    data object Search : Screen("search_screen")
    data object Profile : Screen("profile_screen")
    data object Details : Screen("details_screen/{gameId}") {
        fun createRoute(gameId: Long) = "details_screen/$gameId"
    }
}
