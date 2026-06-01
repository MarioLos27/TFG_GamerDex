package com.mariolos27.gamerdex.presentation.screens.dashboard.model

import com.mariolos27.gamerdex.domain.model.Game

data class DashboardUiState(
    val isLoading: Boolean = true,
    val trendingGames: List<Game> = emptyList(),
    val newReleases: List<Game> = emptyList(),
    val comingSoonGames: List<Game> = emptyList(),
    val topRatedGames: List<Game> = emptyList(),
    val errorMessage: String? = null
)
