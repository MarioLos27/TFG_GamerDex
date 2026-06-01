package com.mariolos27.gamerdex.domain.repository

import com.mariolos27.gamerdex.domain.model.UserGame
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz para el repositorio de registros de juegos de usuario en Firestore.
 */
interface UserGameRepository {
    suspend fun saveGameLog(userGame: UserGame): Result<Unit>
    suspend fun getUserGame(userId: String, gameId: Long): Result<UserGame?>
    fun getAllUserGames(userId: String): Flow<List<UserGame>>
    suspend fun deleteGameLog(userId: String, gameId: Long): Result<Unit>
    fun getUserGameFlow(userId: String, gameId: Long): Flow<Result<UserGame?>>
}
