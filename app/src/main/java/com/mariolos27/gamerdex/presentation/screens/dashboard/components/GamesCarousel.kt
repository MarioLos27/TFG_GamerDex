package com.mariolos27.gamerdex.presentation.screens.dashboard.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import com.mariolos27.gamerdex.domain.model.Game

@Composable
fun GamesCarousel(games: List<Game>, onGameClick: (Long) -> Unit) {
    LazyRow(
        contentPadding = PaddingValues(horizontal = 20.dp),
        horizontalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games) { game ->
            GameCardClean(game = game, onGameClick = onGameClick)
        }
    }
}
