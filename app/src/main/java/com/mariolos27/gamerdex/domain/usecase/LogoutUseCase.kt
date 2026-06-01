package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para cerrar la sesión del usuario.
 *
 * Orquesta la lógica de negocio para desconectar un usuario.
 */
class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(): Result<Unit> {
        return try {
            authRepository.logout()
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

