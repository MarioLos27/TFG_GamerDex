package com.mariolos27.gamerdex.domain.model

/**
 * Modelo de dominio que representa un videojuego.
 * Este modelo es independiente de la fuente de datos
 * 
 * Campos básicos: id, title, coverUrl (usados en búsqueda)
 * Campos extendidos: year, developer, description, rating, genres, etc.
 */
data class Game(
    val id: Long,
    val title: String,
    val coverUrl: String? = null,
    // Campos extendidos para el detalle
    val year: Int? = null,
    val developer: String? = null,
    val description: String? = null,
    val rating: Float? = null,
    val fans: String? = null,
    val reviews: String? = null,
    val genres: List<String> = emptyList(),
    val platforms: List<Platform> = emptyList(),
    val backdropUrl: String? = null,
    val userRating: Int? = null,
    val userRatingDate: String? = null
)

/**
 * Plataforma disponible para jugar un juego
 */
data class Platform(
    val name: String,
    val iconUrl: String = ""
)

