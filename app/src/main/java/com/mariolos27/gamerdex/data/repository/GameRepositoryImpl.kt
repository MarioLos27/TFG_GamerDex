package com.mariolos27.gamerdex.data.repository

import android.util.Log
import com.mariolos27.gamerdex.data.mapper.GameDetailMapper
import com.mariolos27.gamerdex.data.api.IgdbApi
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.repository.GameRepository
import com.mariolos27.gamerdex.data.datasource.local.dao.GameCacheDao
import com.mariolos27.gamerdex.data.datasource.local.entity.GameCacheEntity
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import javax.inject.Inject

/**
 * Implementación del repositorio de juegos.
 * Coordina la obtención de datos desde IGDB y su transformación al modelo de dominio.
 *
 * Responsabilidades:
 * - Construir queries en formato IGDB
 * - Mapear DTOs a modelos de dominio
 * - Manejar errores de la API
 * - Cachear datos localmente con Room
 *
 * IGDB espera un body en formato text/plain, no JSON.
 */
class GameRepositoryImpl @Inject constructor(
    private val igdbApi: IgdbApi,
    private val gameCacheDao: GameCacheDao
) : GameRepository {

    override suspend fun searchGames(query: String): Result<List<Game>> {
        return try {
            // Buscar primero en la caché local
            val cachedGames = gameCacheDao.search(query)
            if (cachedGames.isNotEmpty()) {
                Log.d("GameRepository", "✅ Resultados encontrados en caché local para: $query")
                return Result.success(cachedGames.map { it.toDomain() })
            }

            // Si no hay en caché, buscar en IGDB
            val igdbQuery = buildSearchQuery(query)

            Log.d("GameRepository", " Buscando: $query")
            Log.d("GameRepository", " Query IGDB: $igdbQuery")

            // Convertir String a RequestBody con tipo text/plain
            val requestBody = igdbQuery.toRequestBody("text/plain".toMediaType())
            Log.d("GameRepository", " RequestBody: $igdbQuery")
            Log.d("GameRepository", " Enviando request a IGDB...")

            val response = igdbApi.searchGames(requestBody)
            Log.d("GameRepository", "✅ Respuesta recibida: ${response.size} juegos")

            // Log cada juego recibido
            response.forEach { game ->
                Log.d("GameRepository", "  - ${game.name} (ID: ${game.id}, Cover: ${game.cover?.imageId})")
            }

            // Si la respuesta está vacía, avisar al usuario
            if (response.isEmpty()) {
                Log.w("GameRepository", "⚠️ IGDB devolvió lista vacía - Verifica:")
                Log.w("GameRepository", "  1. Token no ha expirado")
                Log.w("GameRepository", "  2. El nombre del juego es correcto")
                Log.w("GameRepository", "  3. El formato de la query es válido")
                return Result.success(emptyList())
            }

            val games = response.map { dto ->
                val game = dto.toDomain()
                Log.d("GameRepository", " Juego: ${game.title} (URL: ${game.coverUrl})")
                game
            }

            // Guardar en caché para futuras búsquedas
            val cacheEntities = games.map { game ->
                GameCacheEntity(
                    igdbId = game.id,
                    title = game.title,
                    coverUrl = game.coverUrl,
                    rating = game.rating?.toDouble() ?: 0.0,
                    cachedAt = System.currentTimeMillis()
                )
            }
            gameCacheDao.insertAll(cacheEntities)

            Log.d("GameRepository", "✨ Búsqueda completada exitosamente y guardada en caché")
            Result.success(games)
        } catch (httpException: HttpException) {
            // Errores HTTP específicos (401, 400, etc.)
            val errorMessage = when (httpException.code()) {
                401 -> "❌ TOKEN EXPIRADO - Obtén uno nuevo en https://id.twitch.tv/oauth2/authorize"
                400 -> "❌ Query inválida - Verifica el formato de búsqueda"
                429 -> "❌ Rate limit alcanzado - Espera un momento e intenta de nuevo"
                else -> "❌ Error HTTP ${httpException.code()}: ${httpException.message()}"
            }
            Log.e("GameRepository", errorMessage)
            Log.e("GameRepository", "Response: ${httpException.response()?.errorBody()?.string()}")
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error en búsqueda", e)
            Log.e("GameRepository", "Error message: ${e.message}")
            Log.e("GameRepository", "Error type: ${e::class.simpleName}")
            Log.e("GameRepository", "Error cause: ${e.cause}")
            
            // Mensaje más legible para el usuario
            val userMessage = when {
                e.message?.contains("401") == true -> "TOKEN EXPIRADO - Obtén uno nuevo"
                e.message?.contains("Failed to resolve host") == true -> "SIN CONEXIÓN A INTERNET"
                else -> e.message ?: "Error desconocido"
            }
            
            Result.failure(Exception(userMessage, e))
        }
    }

    override suspend fun getGameDetail(igdbId: Long): Result<Game> {
        return try {
            // Construir la query en formato IGDB para obtener detalles completos
            val igdbQuery = buildDetailQuery(igdbId)

            Log.d("GameRepository", " Obteniendo detalles del juego: $igdbId")
            Log.d("GameRepository", " Query IGDB: $igdbQuery")

            // Convertir String a RequestBody con tipo text/plain
            val requestBody = igdbQuery.toRequestBody("text/plain".toMediaType())
            Log.d("GameRepository", " Enviando request a IGDB...")

            val response = igdbApi.getGameDetail(requestBody)
            Log.d("GameRepository", "✅ Respuesta recibida: ${response.size} juego(s)")

            // Si la respuesta está vacía, juego no encontrado
            if (response.isEmpty()) {
                Log.w("GameRepository", "⚠️ IGDB devolvió lista vacía - Juego ID $igdbId no encontrado")
                return Result.failure(Exception("Juego no encontrado en IGDB"))
            }

            val game = response.first().let(GameDetailMapper::mapToDomain)
            Log.d("GameRepository", " Juego mapeado: ${game.title}")
            Log.d("GameRepository", "✨ Detalles obtenidos exitosamente")
            
            Result.success(game)
        } catch (httpException: HttpException) {
            val errorMessage = when (httpException.code()) {
                401 -> "TOKEN EXPIRADO - Obtén uno nuevo en https://id.twitch.tv/oauth2/authorize"
                400 -> "Query inválida"
                429 -> "Rate limit alcanzado - Espera e intenta de nuevo"
                else -> "Error HTTP ${httpException.code()}"
            }
            Log.e("GameRepository", "❌ Error al obtener detalles: $errorMessage")
            Result.failure(Exception(errorMessage))
        } catch (e: Exception) {
            Log.e("GameRepository", "❌ Error al obtener detalles del juego", e)
            val userMessage = when {
                e.message?.contains("Failed to resolve host") == true -> "SIN CONEXIÓN A INTERNET"
                else -> e.message ?: "Error desconocido"
            }
            Result.failure(Exception(userMessage, e))
        }
    }

    /**
     * Construye una query para búsqueda de juegos
     */
    private fun buildSearchQuery(searchTerm: String): String {
        return "fields id, name, cover.image_id; search \"$searchTerm\"; limit 50;"
    }

    /**
     * Construye una query para obtener detalles completos de un juego
     * 
     * La query solicita:
     * - id: identificador del juego
     * - name: nombre del juego
     * - summary: descripción
     * - cover.image_id: ID de la portada
     * - rating: valoración
     * - first_release_date: fecha de lanzamiento (timestamp)
     * - genres.name: géneros del juego
     * - platforms.name: plataformas disponibles
     * - involved_companies.company.name: empresas involucradas (desarrollador)
     * - screenshots.image_id: imágenes de pantallazos
     * - aggregated_rating: valoración agregada
     * - rating_count: número de valoraciones
     */
    private fun buildDetailQuery(igdbId: Long): String {
        return """fields id, name, summary, cover.image_id, rating, first_release_date,
                         genres.name, platforms.name, involved_companies.company.name,
                         screenshots.image_id, aggregated_rating, rating_count;
                  where id = $igdbId;""".trimIndent()
    }
    override suspend fun getTrendingGames(): Result<List<Game>> {
        // Trending: high rating count, high rating, released relatively recently
        val timestamp = (System.currentTimeMillis() / 1000) - (6 * 30 * 24 * 60 * 60) // 6 months ago
        val query = """
            fields id, name, summary, cover.image_id, rating, first_release_date, genres.name, platforms.name, involved_companies.company.name, screenshots.image_id, aggregated_rating, rating_count;
            where first_release_date > $timestamp & rating_count > 100 & rating > 80;
            sort rating desc;
            limit 10;
        """.trimIndent()
        return executeQuery(query)
    }

    override suspend fun getNewReleases(): Result<List<Game>> {
        val currentTimestamp = System.currentTimeMillis() / 1000
        val timestamp = currentTimestamp - (3 * 30 * 24 * 60 * 60) // 3 months ago
        val query = """
            fields id, name, summary, cover.image_id, rating, first_release_date, genres.name, platforms.name, involved_companies.company.name, screenshots.image_id, aggregated_rating, rating_count;
            where first_release_date > $timestamp & first_release_date < $currentTimestamp;
            sort first_release_date desc;
            limit 10;
        """.trimIndent()
        return executeQuery(query)
    }

    override suspend fun getComingSoonGames(): Result<List<Game>> {
        val currentTimestamp = System.currentTimeMillis() / 1000
        val query = """
            fields id, name, summary, cover.image_id, rating, first_release_date, genres.name, platforms.name, involved_companies.company.name, screenshots.image_id, aggregated_rating, rating_count;
            where first_release_date > $currentTimestamp;
            sort first_release_date asc;
            limit 10;
        """.trimIndent()
        return executeQuery(query)
    }

    override suspend fun getTopRatedGames(): Result<List<Game>> {
        val query = """
            fields id, name, summary, cover.image_id, rating, first_release_date, genres.name, platforms.name, involved_companies.company.name, screenshots.image_id, aggregated_rating, rating_count;
            where rating_count > 500 & rating > 90;
            sort rating desc;
            limit 10;
        """.trimIndent()
        return executeQuery(query)
    }

    private suspend fun executeQuery(query: String): Result<List<Game>> {
        return try {
            val requestBody = query.toRequestBody("text/plain".toMediaType())
            val response = igdbApi.getGameDetail(requestBody) // Reusing getGameDetail endpoint which returns List<GameDetailDto>
            
            val games = response.map(GameDetailMapper::mapToDomain)
            Result.success(games)
        } catch (httpException: HttpException) {
            Result.failure(Exception("HTTP Error ${httpException.code()}"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}


