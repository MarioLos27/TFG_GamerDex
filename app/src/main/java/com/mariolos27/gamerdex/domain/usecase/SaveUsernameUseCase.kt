package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para guardar el username del usuario.
 *
 * Orquesta la lógica de negocio para configurar el username después del registro.
 * Responsabilidades:
 * - Validar formato del username (3-15 caracteres, alfanumérico)
 * - Llamar al repositorio para guardar
 * - Propagar errores normalizados
 */
class SaveUsernameUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(username: String): Result<Unit> {
        return try {
            // Validación de caracteres vacíos
            if (username.isBlank()) {
                return Result.failure(
                    IllegalArgumentException("El username no puede estar vacío")
                )
            }

            // Validación de longitud
            if (username.length < 3) {
                return Result.failure(
                    IllegalArgumentException("El username debe tener al menos 3 caracteres")
                )
            }
            if (username.length > 15) {
                return Result.failure(
                    IllegalArgumentException("El username no puede exceder 15 caracteres")
                )
            }

            // Validación de caracteres alfanuméricos (permitir guiones bajos)
            if (!isAlphanumeric(username)) {
                return Result.failure(
                    IllegalArgumentException(
                        "El username solo puede contener letras, números y guiones bajos"
                    )
                )
            }

            // Guardar en repositorio
            userRepository.saveUsername(username.trim())
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    /**
     * Valida que el username contenga solo caracteres alfanuméricos y guiones bajos.
     */
    private fun isAlphanumeric(username: String): Boolean {
        return username.matches("^[a-zA-Z0-9_]+$".toRegex())
    }
}

