package com.mariolos27.gamerdex.domain.model

/**
 * Modelo que representa el registro de un juego por parte de un usuario.
 */
data class UserGame(
    val id: String, // userId_gameId
    val userId: String,
    val gameId: Long,
    val status: GameStatus,
    val rating: Int? = null,
    val review: String? = null,
    val hoursPlayed: Int? = null,
    val platform: String? = null,
    val startDate: Long? = null,
    val completionDate: Long? = null,
    val lastUpdated: Long = System.currentTimeMillis(),
    // Campos de conveniencia para mostrar en listas sin re-consultar IGDB si es necesario
    val gameTitle: String = "",
    val gameCoverUrl: String? = null,
    val isFavorite: Boolean = false
)
