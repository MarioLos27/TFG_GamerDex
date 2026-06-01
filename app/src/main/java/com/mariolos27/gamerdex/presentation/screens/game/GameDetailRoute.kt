package com.mariolos27.gamerdex.presentation.screens.game

import androidx.compose.runtime.Composable
import androidx.hilt.navigation.compose.hiltViewModel

/**
 * Wrapper de la pantalla de detalle de juego para la navegación.
 * Este composable recibe el gameId desde los argumentos de navegación
 * y lo pasa al ViewModel a través de SavedStateHandle.
 *
 * El ViewModel valida el ID y carga los detalles desde la API.
 */
@Composable
fun GameDetailRoute(
    gameId: Long,
    onBackClick: () -> Unit,
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    // El ViewModel recibe el gameId de SavedStateHandle automáticamente
    // gracias a la anotación @HiltViewModel
    GameDetailScreen(
        viewModel = hiltViewModel(),
        gameId = gameId,
        onBackClick = onBackClick,
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToLibrary = onNavigateToLibrary,
        onNavigateToProfile = onNavigateToProfile
    )
}
