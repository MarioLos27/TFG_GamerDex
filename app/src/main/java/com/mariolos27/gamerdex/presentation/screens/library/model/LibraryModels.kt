package com.mariolos27.gamerdex.presentation.screens.library.model

import androidx.compose.ui.graphics.Color

import com.mariolos27.gamerdex.domain.model.GameStatus

data class GameLibraryItem(
    val id: Long,
    val title: String,
    val platform: String,
    val imageUrl: String,
    val progress: Int = 0,
    val totalHours: Float = 0f,
    val status: GameStatus = GameStatus.WISHLIST
)

data class LibraryCategory(
    val id: Int,
    val name: String,
    val count: Int,
    val icon: Int,
    val color: Color,
    val isSelected: Boolean = false
)

