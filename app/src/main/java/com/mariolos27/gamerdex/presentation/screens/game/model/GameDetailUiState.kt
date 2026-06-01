package com.mariolos27.gamerdex.presentation.screens.game.model

import com.mariolos27.gamerdex.domain.model.Game

/**
 * Estados posibles de la pantalla de detalle de juego
 */
sealed class GameDetailUiState {
    
    /**
     * Estado inicial: cargando datos del juego
     */
    data object Loading : GameDetailUiState()
    
    /**
     * Datos del juego cargados exitosamente
     */
    data class Success(val game: Game) : GameDetailUiState()
    
    /**
     * Error al obtener los datos del juego
     */
    data class Error(val message: String) : GameDetailUiState()
}
