package com.mariolos27.gamerdex.presentation.screens.auth.model

import com.mariolos27.gamerdex.domain.model.AppUser

/**
 * Data class que representa el estado de la UI de autenticación.
 *
 * Estados:
 * - Idle: Estado inicial, sin actividad
 * - Loading: Operación en progreso (login/registro)
 * - Success: Operación completada exitosamente, usuario autenticado
 * - Error: Operación falló, contiene mensaje de error
 * - Unauthenticated: Usuario no autenticado (para navegación)
 */
sealed class AuthUiState {
    data object Idle : AuthUiState()
    data object Loading : AuthUiState()
    data class Success(val user: AppUser) : AuthUiState()
    data class Error(val message: String) : AuthUiState()
    data object Unauthenticated : AuthUiState()
}

/**
 * Data class para los campos del formulario de login/registro.
 */
data class AuthFormState(
    val email: String = "",
    val password: String = "",
    val isEmailValid: Boolean = true,
    val isPasswordValid: Boolean = true
)
