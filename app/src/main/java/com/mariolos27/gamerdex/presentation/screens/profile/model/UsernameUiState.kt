package com.mariolos27.gamerdex.presentation.screens.profile.model

/**
 * Estados UI para la pantalla de configuración de username.
 *
 * Representa los diferentes estados en los que puede estar la operación
 * de guardar el username del usuario.
 */
sealed class UsernameUiState {

    /**
     * Estado inicial: no hay operación en curso.
     */
    data object Idle : UsernameUiState()

    /**
     * Estado de carga: se está guardando el username.
     */
    data object Loading : UsernameUiState()

    /**
     * Estado de error: falló la operación.
     *
     * @param message Mensaje de error para mostrar al usuario
     * @param errorCode Código de error para debugging
     */
    data class Error(
        val message: String,
        val errorCode: String? = null
    ) : UsernameUiState()

    /**
     * Estado de éxito: username guardado correctamente.
     *
     * @param username El username que fue guardado
     */
    data class Success(
        val username: String
    ) : UsernameUiState()
}
