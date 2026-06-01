package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.AppUser
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import javax.inject.Inject

/**
 * Caso de uso para el registro de un nuevo usuario.
 *
 * Orquesta la lógica de negocio para crear una nueva cuenta.
 * Responsabilidades:
 * - Validar entrada (email válido, contraseña fuerte)
 * - Llamar al repositorio para registrar
 * - Propagar errores normalizados
 */
class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): Result<AppUser> {
        return try {
            // Validaciones básicas
            if (email.isBlank()) {
                return Result.failure(IllegalArgumentException("El email no puede estar vacío"))
            }
            if (!isValidEmail(email)) {
                return Result.failure(IllegalArgumentException("El email no es válido"))
            }
            if (password.isBlank()) {
                return Result.failure(IllegalArgumentException("La contraseña no puede estar vacía"))
            }
            if (password.length < 6) {
                return Result.failure(IllegalArgumentException("La contraseña debe tener al menos 6 caracteres"))
            }

            authRepository.register(email.trim(), password)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Validación simple de email usando regex.
     */
    private fun isValidEmail(email: String): Boolean {
        val emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$"
        return email.matches(emailRegex.toRegex())
    }
}

