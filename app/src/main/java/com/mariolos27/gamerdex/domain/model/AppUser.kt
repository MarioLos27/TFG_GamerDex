package com.mariolos27.gamerdex.domain.model

/**
 * Modelo de dominio que representa un usuario de la aplicación.
 *
 * @param id Identificador único del usuario (UID de Firebase)
 * @param email Correo electrónico del usuario
 * @param username Nombre de usuario único en la plataforma
 * @param createdAt Fecha de creación de la cuenta (opcional)
 */
data class AppUser(
    val id: String,
    val email: String,
    val username: String? = null,
    val createdAt: String? = null,
    val bio: String? = null,
    val profileImageUrl: String? = null,
    val gamesCount: Int = 0,
    val reviewsCount: Int = 0,
    val listsCount: Int = 0,
    val followersCount: Int = 0
)

