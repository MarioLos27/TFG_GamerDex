package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Caso de uso para obtener los detalles completos de un videojuego.
 * Inyectable vía Hilt.
 * 
 * Responsabilidades:
 * - Validar que el ID sea válido
 * - Llamar al repositorio de juegos
 * - Manejar y propagar errores
 */
class GetGameDetailUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * Obtiene los detalles de un juego por su ID de IGDB
     * 
     * @param igdbId ID del juego en IGDB
     * @return Result con el juego o el error
     */
    suspend operator fun invoke(igdbId: Long): Result<Game> {
        return when {
            igdbId <= 0 -> Result.failure(Exception("ID de juego inválido: $igdbId"))
            else -> gameRepository.getGameDetail(igdbId)
        }
    }
}
