package com.mariolos27.gamerdex.domain.repository

/**
 * Interfaz del repositorio de usuario.
 *
 * Define los contratos para operaciones de gestión de perfil de usuario.
 * Mantiene los detalles de implementación separados de la lógica de negocio.
 *
 * Responsabilidades:
 * - Guardar/actualizar datos de perfil en Firestore
 * - Verificar disponibilidad de username
 * - Obtener datos de perfil del usuario actual
 */
interface UserRepository {

    /**
     * Guarda el username del usuario actual en Firestore.
     *
     * @param username El nombre de usuario a guardar (debe ser único)
     * @return Result<Unit> indicando si la operación fue exitosa
     * @throws UserException si el username no está disponible o falla la operación
     */
    suspend fun saveUsername(username: String): Result<Unit>

    /**
     * Verifica si un username está disponible (no está en uso).
     *
     * @param username El nombre de usuario a verificar
     * @return Result<Boolean> true si está disponible, false si está en uso
     */
    suspend fun isUsernameAvailable(username: String): Result<Boolean>

    /**
     * Obtiene el username del usuario actual desde Firestore.
     *
     * @return Result<String?> con el username o null si no está configurado
     */
    suspend fun getCurrentUsername(): Result<String?>

    /**
     * Actualiza el perfil completo del usuario actual en Firestore.
     *
     * @param username El nombre de usuario (debe ser único)
     * @param bio Biografía del usuario
     * @param profileImageUrl URL de la imagen de perfil
     * @return Result<Unit> indicando si la operación fue exitosa
     */
    suspend fun updateUserProfile(
        username: String,
        bio: String,
        profileImageUrl: String
    ): Result<Unit>

    /**
     * Sube una imagen de perfil a Firebase Storage y retorna su URL pública.
     *
     * @param imageUri Uri local de la imagen seleccionada por el usuario.
     * @return Result<String> con la URL de descarga o un error.
     */
    suspend fun uploadProfileImage(imageUri: android.net.Uri): Result<String>
}

/**
 * Excepción personalizada para errores relacionados con el perfil de usuario.
 */
class UserException(
    val errorCode: String? = null,
    message: String = "Error en operación de usuario"
) : Exception(message)

