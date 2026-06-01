package com.mariolos27.gamerdex.presentation.screens.search.model

import com.mariolos27.gamerdex.domain.model.Game

/**
 * Contiene todos los posibles estados de la pantalla de búsqueda.
 * Gestiona la reactibilidad usando sealed class.
 */
sealed class SearchUiState {
    /**
     * Estado inicial: sin búsqueda
     */
    data object Idle : SearchUiState()
    
    /**
     * Estado de carga: búsqueda en progreso
     */
    data object Loading : SearchUiState()
    
    /**
     * Estado de éxito: resultados disponibles
     */
    data class Success(
        val games: List<Game>
    ) : SearchUiState()
    
    /**
     * Estado de error: algo salió mal en la búsqueda
     */
    data class Error(
        val message: String,
        val exception: Throwable? = null
    ) : SearchUiState()
}
