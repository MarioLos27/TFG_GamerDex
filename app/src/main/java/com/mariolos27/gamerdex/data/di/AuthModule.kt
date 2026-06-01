package com.mariolos27.gamerdex.data.di

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.mariolos27.gamerdex.data.repository.AuthRepositoryImpl
import com.mariolos27.gamerdex.data.repository.UserRepositoryImpl
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.repository.UserRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

/**
 * Módulo Hilt para la inyección de dependencias de Autenticación.
 *
 * Define cómo construir las instancias de:
 * - FirebaseAuth: cliente de autenticación
 * - FirebaseFirestore: base de datos
 * - AuthRepository: implementación con Firebase
 *
 * Nota: Firebase se inicializa automáticamente desde google-services.json
 */
@Module
@InstallIn(SingletonComponent::class)
object AuthModule {

    @Provides
    @Singleton
    fun provideFirebaseAuth(): FirebaseAuth {
        return FirebaseAuth.getInstance()
    }

    @Provides
    @Singleton
    fun provideFirebaseFirestore(): FirebaseFirestore {
        return FirebaseFirestore.getInstance()
    }

    /**
     * Proporciona la implementación del repositorio de autenticación.
     *
     * Responsabilidades:
     * - Orquestar llamadas a Firebase Auth
     * - Gestionar datos de usuario en Firestore
     * - Mapear respuestas a modelos de dominio
     * - Normalizar errores
     */
    @Provides
    @Singleton
    fun provideAuthRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore
    ): AuthRepository {
        return AuthRepositoryImpl(firebaseAuth, firebaseFirestore)
    }

    /**
     * Proporciona la implementación del repositorio de usuario.
     *
     * Responsabilidades:
     * - Gestionar operaciones de perfil (username, etc.)
     * - Verificar disponibilidad de usernames
     * - Mantener integridad de datos en Firestore
     */
    @Provides
    @Singleton
    fun provideUserRepository(
        firebaseAuth: FirebaseAuth,
        firebaseFirestore: FirebaseFirestore,
        @dagger.hilt.android.qualifiers.ApplicationContext context: android.content.Context
    ): UserRepository {
        return UserRepositoryImpl(firebaseAuth, firebaseFirestore, context)
    }
}


