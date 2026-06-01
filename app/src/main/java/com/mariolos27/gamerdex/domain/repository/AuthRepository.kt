package com.mariolos27.gamerdex.domain.repository

import com.mariolos27.gamerdex.domain.model.AppUser
import kotlinx.coroutines.flow.Flow

/**
 * Interfaz del repositorio de autenticación.
 *
 * Define los contratos para las operaciones de autenticación sin detalles
 * de implementación. Esto permite que la lógica de negocio sea agnóstica
 * a la fuente de datos.
 *
 * Responsabilidades:
 * - Autenticación de usuarios (login, registro)
 * - Gestión de sesiones
 * - Manejo de errores normalizados
 */
interface AuthRepository {

    /**
     * Realiza el login del usuario con email y contraseña.
     *
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return Result<AppUser> con el usuario logueado o error
     * @throws AuthException con mensaje descriptivo del error
     */
    suspend fun login(email: String, password: String): Result<AppUser>

    /**
     * Registra un nuevo usuario con email y contraseña.
     *
     * @param email Correo electrónico del usuario
     * @param password Contraseña del usuario
     * @return Result<AppUser> con el usuario registrado o error
     * @throws AuthException con mensaje descriptivo del error
     */
    suspend fun register(email: String, password: String): Result<AppUser>

    /**
     * Cierra la sesión del usuario actual.
     *
     * @return Result<Unit> indicando si la operación fue exitosa
     */
    suspend fun logout(): Result<Unit>

    /**
     * Obtiene el usuario actualmente logueado.
     *
     * @return Flow que emite el usuario actual o null si no hay usuario logueado
     */
    fun getCurrentUser(): Flow<AppUser?>

    /**
     * Verifica si hay un usuario actualmente logueado.
     *
     * @return true si hay usuario activo, false en caso contrario
     */
    suspend fun isUserLoggedIn(): Boolean
}

/**
 * Excepción personalizada para errores de autenticación.
 * Normaliza los errores de diferentes fuentes (Firebase, etc.) en mensajes
 * comprensibles para la UI.
 */
class AuthException(
    val errorCode: String? = null,
    message: String = "Error en autenticación"
) : Exception(message)

