package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import javax.inject.Inject

/**
 * Caso de uso para obtener todos los juegos registrados por el usuario actual.
 * Permite filtrar por estado.
 */
class GetAllUserGamesUseCase @Inject constructor(
    private val repository: UserGameRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(status: GameStatus? = null): Flow<List<UserGame>> {
        return authRepository.getCurrentUser().flatMapLatest { user ->
            val userId = user?.id
            if (userId != null) {
                repository.getAllUserGames(userId).map { games ->
                    if (status != null) {
                        games.filter { it.status == status }
                    } else {
                        games
                    }
                }
            } else {
                flowOf(emptyList())
            }
        }
    }
}
