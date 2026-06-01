package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar o actualizar el registro de un juego.
 * Valida reglas de negocio básicas antes de persistir.
 */
class SaveGameLogUseCase @Inject constructor(
    private val repository: UserGameRepository
) {
    suspend operator fun invoke(userGame: UserGame): Result<Unit> {
        // Validaciones de negocio
        if (userGame.rating != null && (userGame.rating < 0 || userGame.rating > 10)) {
            return Result.failure(Exception("La puntuación debe estar entre 0 y 10"))
        }

        if (userGame.status == GameStatus.COMPLETED && userGame.completionDate == null) {
            // Podríamos auto-asignar la fecha actual si falta
            return repository.saveGameLog(userGame.copy(completionDate = System.currentTimeMillis()))
        }

        return repository.saveGameLog(userGame)
    }
}
