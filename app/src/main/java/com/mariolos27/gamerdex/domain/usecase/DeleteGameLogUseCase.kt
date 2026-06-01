package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import javax.inject.Inject

/**
 * Caso de uso para eliminar el registro de un juego de un usuario.
 */
class DeleteGameLogUseCase @Inject constructor(
    private val repository: UserGameRepository
) {
    suspend operator fun invoke(userId: String, gameId: Long): Result<Unit> {
        return repository.deleteGameLog(userId, gameId)
    }
}
