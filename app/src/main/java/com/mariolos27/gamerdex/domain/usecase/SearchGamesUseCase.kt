package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.repository.GameRepository
import javax.inject.Inject

/**
 * Caso de uso para buscar juegos.
 * Contiene la lógica de negocio para la búsqueda de videojuegos.
 * Inyectable vía Hilt.
 */
class SearchGamesUseCase @Inject constructor(
    private val gameRepository: GameRepository
) {
    /**
     * Ejecuta la búsqueda de juegos
     * @param query Término de búsqueda
     * @return Result con la lista de juegos o el error
     */
    suspend operator fun invoke(query: String): Result<List<Game>> {
        return if (query.isBlank()) {
            Result.success(emptyList())
        } else {
            gameRepository.searchGames(query)
        }
    }
}

