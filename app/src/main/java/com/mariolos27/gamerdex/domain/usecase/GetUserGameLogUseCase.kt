package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import kotlinx.coroutines.flow.firstOrNull
import javax.inject.Inject

/**
 * Caso de uso para obtener el registro de un juego específico del usuario actual.
 */
class GetUserGameLogUseCase @Inject constructor(
    private val repository: UserGameRepository,
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(gameId: Long): Result<UserGame?> {
        val user = authRepository.getCurrentUser().firstOrNull()
        val userId = user?.id 
            ?: return Result.failure(Exception("Usuario no autenticado"))
            
        return repository.getUserGame(userId, gameId)
    }
}
