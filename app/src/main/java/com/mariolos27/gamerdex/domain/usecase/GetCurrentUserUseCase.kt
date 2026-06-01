package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.AppUser
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/**
 * Caso de uso para obtener el usuario actual logueado.
 *
 * Proporciona un Flow que emite cambios en el estado de autenticación.
 */
class GetCurrentUserUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    operator fun invoke(): Flow<AppUser?> {
        return authRepository.getCurrentUser()
    }
}

