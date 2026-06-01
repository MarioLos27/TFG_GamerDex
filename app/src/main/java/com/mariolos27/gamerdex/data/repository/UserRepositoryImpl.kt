package com.mariolos27.gamerdex.data.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import java.io.ByteArrayOutputStream
import java.io.InputStream
import com.mariolos27.gamerdex.domain.repository.UserException
import com.mariolos27.gamerdex.domain.repository.UserRepository
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "UserRepositoryImpl"
private const val USERS_COLLECTION = "users"
private const val USERNAMES_COLLECTION = "usernames"

/**
 * Implementación del repositorio de usuario usando Firebase (Auth + Firestore).
 *
 * Responsabilidades:
 * - Orquestar llamadas a Firestore para guardar/actualizar perfil
 * - Verificar disponibilidad de username en colección separada
 * - Normalizar errores a UserException
 * - Mantener integridad de datos entre colecciones
 */
class UserRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore,
    private val context: Context
) : UserRepository {

    /**
     * Guarda el username del usuario actual en Firestore.
     *
     * Flujo:
     * 1. Obtiene el UID del usuario actual
     * 2. Verifica disponibilidad del username
     * 3. Actualiza el documento del usuario con el username
     * 4. Registra el username en colección de usernames para búsquedas rápidas
     * 5. Si falla, realiza rollback
     */
    override suspend fun saveUsername(username: String): Result<Unit> {
        return try {
            val currentUid = firebaseAuth.currentUser?.uid
                ?: throw UserException(
                    errorCode = "USER_NOT_AUTHENTICATED",
                    message = "No hay usuario autenticado"
                )

            Log.d(TAG, "Guardando username '$username' para usuario $currentUid")

            // Verificar disponibilidad
            val available = isUsernameAvailable(username).getOrNull()
                ?: throw UserException(
                    errorCode = "AVAILABILITY_CHECK_FAILED",
                    message = "No se pudo verificar la disponibilidad del username"
                )

            if (!available) {
                return Result.failure(
                    UserException(
                        errorCode = "USERNAME_TAKEN",
                        message = "El username ya está en uso"
                    )
                )
            }

            // Actualizar documento del usuario
            firebaseFirestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .update(mapOf("username" to username.lowercase()))
                .await()

            // Registrar username en colección separada para búsquedas
            firebaseFirestore.collection(USERNAMES_COLLECTION)
                .document(username.lowercase())
                .set(
                    mapOf(
                        "uid" to currentUid,
                        "createdAt" to System.currentTimeMillis()
                    )
                )
                .await()

            Log.d(TAG, "Username guardado exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error al guardar username", e)
            Result.failure(normalizeError(e, "Error al guardar el username"))
        }
    }

    /**
     * Verifica si un username está disponible.
     *
     * Consulta la sub-colección de usernames para ver si ya existe.
     * Los usernames son case-insensitive.
     */
    override suspend fun isUsernameAvailable(username: String): Result<Boolean> {
        return try {
            val doc = firebaseFirestore.collection(USERNAMES_COLLECTION)
                .document(username.lowercase())
                .get()
                .await()

            Result.success(!doc.exists())
        } catch (e: Exception) {
            Log.e(TAG, "Error al verificar disponibilidad de username", e)
            Result.failure(normalizeError(e, "Error al verificar disponibilidad"))
        }
    }

    /**
     * Obtiene el username del usuario actual.
     *
     * Lee del documento principal de usuario en Firestore.
     */
    override suspend fun getCurrentUsername(): Result<String?> {
        return try {
            val currentUid = firebaseAuth.currentUser?.uid
                ?: throw UserException(
                    errorCode = "USER_NOT_AUTHENTICATED",
                    message = "No hay usuario autenticado"
                )

            val doc = firebaseFirestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .get()
                .await()

            val username = doc.getString("username")
            Result.success(username)
        } catch (e: Exception) {
            Log.e(TAG, "Error al obtener username", e)
            Result.failure(normalizeError(e, "Error al obtener username"))
        }
    }

    /**
     * Actualiza el perfil completo del usuario actual en Firestore.
     *
     * Flujo:
     * 1. Obtiene el UID y el username actual
     * 2. Si el username cambió, verifica disponibilidad y actualiza colección de usernames
     * 3. Actualiza username, bio y profileImageUrl en el documento del usuario
     */
    override suspend fun updateUserProfile(
        username: String,
        bio: String,
        profileImageUrl: String
    ): Result<Unit> {
        return try {
            val currentUid = firebaseAuth.currentUser?.uid
                ?: throw UserException(
                    errorCode = "USER_NOT_AUTHENTICATED",
                    message = "No hay usuario autenticado"
                )

            Log.d(TAG, "Actualizando perfil para usuario $currentUid")

            // Obtener el username actual para detectar si cambió
            val currentDoc = firebaseFirestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .get()
                .await()
            val currentUsername = currentDoc.getString("username") ?: ""
            val normalizedNewUsername = username.lowercase().trim()

            // Si el username cambió, verificar disponibilidad
            if (currentUsername != normalizedNewUsername && normalizedNewUsername.isNotEmpty()) {
                val available = isUsernameAvailable(normalizedNewUsername).getOrNull()
                    ?: throw UserException(
                        errorCode = "AVAILABILITY_CHECK_FAILED",
                        message = "No se pudo verificar la disponibilidad del username"
                    )

                if (!available) {
                    return Result.failure(
                        UserException(
                            errorCode = "USERNAME_TAKEN",
                            message = "El username ya está en uso"
                        )
                    )
                }

                // Eliminar el username viejo de la colección de usernames
                if (currentUsername.isNotEmpty()) {
                    try {
                        firebaseFirestore.collection(USERNAMES_COLLECTION)
                            .document(currentUsername)
                            .delete()
                            .await()
                    } catch (e: Exception) {
                        Log.w(TAG, "No se pudo eliminar el username viejo", e)
                    }
                }

                // Registrar el nuevo username
                firebaseFirestore.collection(USERNAMES_COLLECTION)
                    .document(normalizedNewUsername)
                    .set(
                        mapOf(
                            "uid" to currentUid,
                            "createdAt" to System.currentTimeMillis()
                        )
                    )
                    .await()
            }

            // Actualizar documento del usuario con todos los campos
            val updates = mapOf(
                "username" to normalizedNewUsername,
                "bio" to bio,
                "profileImageUrl" to profileImageUrl
            )

            firebaseFirestore.collection(USERS_COLLECTION)
                .document(currentUid)
                .update(updates)
                .await()

            Log.d(TAG, "Perfil actualizado exitosamente")
            Result.success(Unit)

        } catch (e: Exception) {
            Log.e(TAG, "Error al actualizar perfil", e)
            Result.failure(normalizeError(e, "Error al actualizar el perfil"))
        }
    }

    override suspend fun uploadProfileImage(imageUri: android.net.Uri): Result<String> {
        return try {
            val currentUid = firebaseAuth.currentUser?.uid
                ?: throw UserException(
                    errorCode = "USER_NOT_AUTHENTICATED",
                    message = "No hay usuario autenticado"
                )

            // Abrir el InputStream usando el ContentResolver
            val inputStream: InputStream? = context.contentResolver.openInputStream(imageUri)
            if (inputStream == null) {
                return Result.failure(UserException(message = "No se pudo leer la imagen seleccionada"))
            }

            // Decodificar el Bitmap
            val originalBitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (originalBitmap == null) {
                return Result.failure(UserException(message = "No se pudo decodificar la imagen"))
            }

            // Redimensionar para no exceder el límite de Firestore (1 MB máx por documento, así que usaremos ~256x256)
            val maxSize = 256
            var width = originalBitmap.width
            var height = originalBitmap.height

            val bitmapRatio = width.toFloat() / height.toFloat()
            if (bitmapRatio > 1) {
                width = maxSize
                height = (width / bitmapRatio).toInt()
            } else {
                height = maxSize
                width = (height * bitmapRatio).toInt()
            }

            val scaledBitmap = Bitmap.createScaledBitmap(originalBitmap, width, height, true)

            // Comprimir el Bitmap a JPEG (calidad 60)
            val outputStream = ByteArrayOutputStream()
            scaledBitmap.compress(Bitmap.CompressFormat.JPEG, 60, outputStream)
            val byteArray = outputStream.toByteArray()

            // Codificar a Base64
            val base64String = Base64.encodeToString(byteArray, Base64.NO_WRAP)
            
            // Prefijo necesario para que Coil o un navegador entienda que es una imagen en Base64
            val dataUri = "data:image/jpeg;base64,$base64String"

            Log.d(TAG, "Imagen comprimida a Base64 exitosamente. Longitud: ${dataUri.length} chars")
            
            Result.success(dataUri)
        } catch (e: Exception) {
            Log.e(TAG, "Error al procesar imagen a Base64", e)
            Result.failure(normalizeError(e, "Error al procesar la imagen de perfil"))
        }
    }

    /**
     * Normaliza los errores de Firebase a mensajes descriptivos.
     */
    private fun normalizeError(error: Exception, defaultMessage: String): UserException {
        val errorMessage = error.message ?: ""
        Log.e(TAG, "Firebase Error: $errorMessage", error)

        val (errorCode, userFriendlyMessage) = when {
            errorMessage.contains("Permission denied", ignoreCase = true) ->
                "FIRESTORE_PERMISSION" to "Error de permisos. Intenta de nuevo más tarde."

            errorMessage.contains("INTERNAL", ignoreCase = true) ->
                "FIREBASE_INTERNAL" to "Error interno de Firebase. Intenta de nuevo."

            errorMessage.contains("not found", ignoreCase = true) ->
                "DOCUMENT_NOT_FOUND" to "No se encontraron datos del usuario."

            else -> "UNKNOWN_ERROR" to defaultMessage
        }

        return UserException(
            errorCode = errorCode,
            message = userFriendlyMessage
        )
    }
}


