package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.AppUser
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el login del usuario.
 *
 * Orquesta la lógica de negocio para autenticar un usuario.
 * Responsabilidades:
 * - Validar entrada (email y contraseña no vacíos)
 * - Llamar al repositorio para autenticar
 * - Propagar errores normalizados
 */
class LoginUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser> {
        return try {
            // Validaciones básicas
            if (email.isBlank()) {
                return Result.failure(IllegalArgumentException("El email no puede estar vacío"))
            }
            if (password.isBlank()) {
                return Result.failure(IllegalArgumentException("La contraseña no puede estar vacía"))
            }

            authRepository.login(email.trim(), password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}

