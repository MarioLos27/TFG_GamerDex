package com.mariolos27.gamerdex.presentation.screens.profile.model

import com.mariolos27.gamerdex.domain.model.AppUser

sealed class ProfileUiState {
    data object Loading : ProfileUiState()
    data class Success(
        val user: AppUser,
        val recentGames: List<com.mariolos27.gamerdex.domain.model.UserGame> = emptyList(),
        val favoriteGames: List<com.mariolos27.gamerdex.domain.model.UserGame> = emptyList()
    ) : ProfileUiState()
    data class Error(val message: String) : ProfileUiState()
}
