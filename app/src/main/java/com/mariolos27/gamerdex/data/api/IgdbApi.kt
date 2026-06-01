package com.mariolos27.gamerdex.data.api

import com.mariolos27.gamerdex.data.api.dto.GameDto
import com.mariolos27.gamerdex.data.api.dto.GameDetailDto
import okhttp3.RequestBody
import retrofit2.http.POST
import retrofit2.http.Body

/**
 * Interfaz de Retrofit para la API de IGDB.
 */
interface IgdbApi {
    /**
     * Búsqueda de juegos en IGDB mediante una consulta personalizada.
     *
     * @param query RequestBody con la consulta
     * @return Lista de juegos encontrados
     */
    @POST("games")
    suspend fun searchGames(
        @Body query: RequestBody
    ): List<GameDto>

    /**
     * Obtiene los detalles completos de un juego por su ID.
     *
     * @param query RequestBody con la consulta
     * @return Lista con un único juego
     */
    @POST("games")
    suspend fun getGameDetail(
        @Body query: RequestBody
    ): List<GameDetailDto>
}


