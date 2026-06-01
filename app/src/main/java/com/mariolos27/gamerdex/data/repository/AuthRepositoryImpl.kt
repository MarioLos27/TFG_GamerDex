package com.mariolos27.gamerdex.data.repository

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.mariolos27.gamerdex.domain.model.AppUser
import com.mariolos27.gamerdex.domain.repository.AuthException
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "AuthRepositoryImpl"

/**
 * Implementación del repositorio de autenticación usando Firebase Auth y Firestore.
 */
class AuthRepositoryImpl @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firebaseFirestore: FirebaseFirestore
) : AuthRepository {

    /**
     * Inicia sesión con email y contraseña, recuperando los datos del usuario.
     */
    override suspend fun login(email: String, password: String): Result<AppUser> {
        return try {
            val authResult = firebaseAuth.signInWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw AuthException(
                errorCode = "USER_NULL",
                message = "No se pudo obtener los datos del usuario"
            )

            val appUser = firebaseUserToAppUser(firebaseUser)
            Result.success(appUser)
        } catch (e: Exception) {
            Result.failure(normalizeError(e, "Error en el inicio de sesión"))
        }
    }

    /**
     * Registra un nuevo usuario con email y contraseña.
     */
    override suspend fun register(email: String, password: String): Result<AppUser> {
        return try {
            Log.d(TAG, "Iniciando registro para: $email")
            val authResult = firebaseAuth.createUserWithEmailAndPassword(email, password).await()
            val firebaseUser = authResult.user ?: throw AuthException(
                errorCode = "USER_NULL",
                message = "No se pudo obtener los datos del usuario"
            )

            Log.d(TAG, "Usuario creado en Auth: ${firebaseUser.uid}")

            // Crear documento en Firestore
            try {
                val userDocument = mapOf(
                    "id" to firebaseUser.uid,
                    "email" to firebaseUser.email,
                    "username" to "",  // Campo vacío hasta que configure su username
                    "createdAt" to System.currentTimeMillis(),
                    "displayName" to ""
                )
                firebaseFirestore.collection("users").document(firebaseUser.uid)
                    .set(userDocument)
                    .await()
                Log.d(TAG, "Documento creado en Firestore")
            } catch (firestoreError: Exception) {
                Log.e(TAG, "Error al crear documento en Firestore", firestoreError)
                // Si falla la creación en Firestore, eliminamos el usuario de Auth
                try {
                    firebaseAuth.currentUser?.delete()?.await()
                    Log.d(TAG, "Usuario eliminado de Auth debido a error en Firestore")
                } catch (deleteError: Exception) {
                    Log.e(TAG, "Error al eliminar usuario de Auth", deleteError)
                }
                throw firestoreError
            }

            val appUser = firebaseUserToAppUser(firebaseUser)
            Result.success(appUser)
        } catch (e: Exception) {
            Result.failure(normalizeError(e, "Error en el registro"))
        }
    }

    /**
     * Cierra la sesión del usuario actual.
     */
    override suspend fun logout(): Result<Unit> {
        return try {
            firebaseAuth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(AuthException(
                errorCode = "LOGOUT_FAILED",
                message = "No se pudo cerrar la sesión"
            ))
        }
    }

    /**
     * Obtiene el flujo del usuario actual, actualizado en tiempo real.
     */
    override fun getCurrentUser(): Flow<AppUser?> = flow {
        // Emitir el usuario actual inicial
        val currentUser = firebaseAuth.currentUser
        if (currentUser != null) {
            emit(firestoreUserToAppUser(currentUser))
        } else {
            emit(null)
        }
    }

    /**
     * Verifica si hay un usuario actualmente logueado.
     */
    override suspend fun isUserLoggedIn(): Boolean {
        return firebaseAuth.currentUser != null
    }

    /**
     * Convierte FirebaseUser a AppUser.
     * Obtiene información adicional de Firestore si existe.
     */
    private suspend fun firebaseUserToAppUser(firebaseUser: FirebaseUser): AppUser {
        return try {
            val doc = firebaseFirestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            AppUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = doc.getString("username"),
                createdAt = doc.getLong("createdAt")?.toString() ?: System.currentTimeMillis().toString(),
                bio = doc.getString("bio"),
                profileImageUrl = doc.getString("profileImageUrl"),
                gamesCount = doc.getLong("gamesCount")?.toInt() ?: 0,
                reviewsCount = doc.getLong("reviewsCount")?.toInt() ?: 0,
                listsCount = doc.getLong("listsCount")?.toInt() ?: 0,
                followersCount = doc.getLong("followersCount")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            // Si no existe en Firestore, retorna con valores básicos
            AppUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                createdAt = System.currentTimeMillis().toString()
            )
        }
    }

    /**
     * Convierte FirebaseUser a AppUser para Flow.
     * Lee datos completos de Firestore.
     */
    private suspend fun firestoreUserToAppUser(firebaseUser: FirebaseUser): AppUser {
        return try {
            val doc = firebaseFirestore.collection("users")
                .document(firebaseUser.uid)
                .get()
                .await()

            AppUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                username = doc.getString("username"),
                createdAt = doc.getLong("createdAt")?.toString() ?: System.currentTimeMillis().toString(),
                bio = doc.getString("bio"),
                profileImageUrl = doc.getString("profileImageUrl"),
                gamesCount = doc.getLong("gamesCount")?.toInt() ?: 0,
                reviewsCount = doc.getLong("reviewsCount")?.toInt() ?: 0,
                listsCount = doc.getLong("listsCount")?.toInt() ?: 0,
                followersCount = doc.getLong("followersCount")?.toInt() ?: 0
            )
        } catch (e: Exception) {
            AppUser(
                id = firebaseUser.uid,
                email = firebaseUser.email ?: "",
                createdAt = System.currentTimeMillis().toString()
            )
        }
    }

    /**
     * Mapea excepciones de Firebase a dominio.
     */
    private fun normalizeError(error: Exception, defaultMessage: String): AuthException {
        val errorMessage = error.message ?: ""
        Log.e(TAG, "Firebase Error: $errorMessage", error)

        val (errorCode, userFriendlyMessage) = when {
            errorMessage.contains("There is no user record corresponding to this email", ignoreCase = true) ||
            errorMessage.contains("EMAIL_NOT_FOUND", ignoreCase = true) ->
                "INVALID_EMAIL" to "El email no está registrado"

            errorMessage.contains("The password is invalid", ignoreCase = true) ||
            errorMessage.contains("INVALID_PASSWORD", ignoreCase = true) ->
                "INVALID_PASSWORD" to "La contraseña es incorrecta"

            errorMessage.contains("The email address is already in use", ignoreCase = true) ||
            errorMessage.contains("EMAIL_ALREADY_IN_USE", ignoreCase = true) ->
                "EMAIL_ALREADY_IN_USE" to "El email ya está registrado"

            errorMessage.contains("The given password is invalid", ignoreCase = true) ||
            errorMessage.contains("WEAK_PASSWORD", ignoreCase = true) ->
                "WEAK_PASSWORD" to "La contraseña debe tener al menos 6 caracteres"

            errorMessage.contains("The email address is badly formatted", ignoreCase = true) ||
            errorMessage.contains("INVALID_EMAIL", ignoreCase = true) ->
                "INVALID_EMAIL" to "El email no es válido"

            errorMessage.contains("Permission denied", ignoreCase = true) ->
                "FIRESTORE_PERMISSION" to "Error de permisos en la base de datos. Por favor, contacta con soporte."

            errorMessage.contains("INTERNAL", ignoreCase = true) ->
                "FIREBASE_INTERNAL" to "Error interno de Firebase. Por favor, intenta de nuevo."

            else -> "UNKNOWN_ERROR" to defaultMessage
        }

        return AuthException(
            errorCode = errorCode,
            message = userFriendlyMessage
        )
    }
}

