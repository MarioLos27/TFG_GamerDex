package com.mariolos27.gamerdex.domain.repository

import com.mariolos27.gamerdex.domain.model.Game

/**
 * Contrato del repositorio de juegos.
 * Define las operaciones que se pueden realizar con los datos de juegos.
 * La implementación está en la capa Data.
 */
interface GameRepository {
    /**
     * Busca juegos por query en la API de IGDB
     * @param query Término de búsqueda
     * @return Lista de juegos encontrados
     */
    suspend fun searchGames(query: String): Result<List<Game>>

    /**
     * Obtiene los detalles completos de un juego por su ID de IGDB
     * @param igdbId ID del juego en IGDB
     * @return Juego con todos sus detalles
     */
    suspend fun getGameDetail(igdbId: Long): Result<Game>

    /**
     * Obtiene los juegos en tendencia (buena puntuación y recientes)
     */
    suspend fun getTrendingGames(): Result<List<Game>>

    /**
     * Obtiene los juegos recién lanzados
     */
    suspend fun getNewReleases(): Result<List<Game>>

    /**
     * Obtiene los juegos que se lanzarán próximamente
     */
    suspend fun getComingSoonGames(): Result<List<Game>>

    /**
     * Obtiene los juegos mejor valorados
     */
    suspend fun getTopRatedGames(): Result<List<Game>>
}

