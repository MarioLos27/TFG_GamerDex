package com.mariolos27.gamerdex.presentation.screens.game.model

import com.mariolos27.gamerdex.domain.model.UserGame

sealed class GameLogUiState {
    object Idle : GameLogUiState()
    object Loading : GameLogUiState()
    data class Success(val userGame: UserGame?) : GameLogUiState()
    data class Error(val message: String) : GameLogUiState()
    object Saved : GameLogUiState()
    object Deleted : GameLogUiState()
}
