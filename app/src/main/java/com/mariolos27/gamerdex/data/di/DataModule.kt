package com.mariolos27.gamerdex.data.di

import com.google.firebase.firestore.FirebaseFirestore
import com.mariolos27.gamerdex.data.api.IgdbApi
import com.mariolos27.gamerdex.data.repository.GameRepositoryImpl
import com.mariolos27.gamerdex.data.repository.UserGameRepositoryImpl
import com.mariolos27.gamerdex.domain.repository.GameRepository
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import retrofit2.Retrofit
import javax.inject.Singleton

/**
 * Módulo Hilt para la inyección de dependencias de la capa Data.
 *
 * Define cómo construir las instancias de:
 * - IgdbApi (servicio de API, ya tiene Retrofit configurado en NetworkModule)
 * - GameRepository (repositorio)
 * - UserGameRepository (repositorio de juegos de usuario)
 *
 * Nota: Retrofit se crea en NetworkModule para tener acceso a OkHttpClient
 * con interceptores de autenticación.
 */
@Module
@InstallIn(SingletonComponent::class)
object DataModule {

    /**
     * Proporciona la interfaz IgdbApi usando Retrofit.
     *
     * El Retrofit recibido ya tiene configurado:
     * - Base URL: https://api.igdb.com/v4/
     * - OkHttpClient con interceptor de autenticación
     * - GsonConverterFactory para JSON
     */
    @Provides
    @Singleton
    fun provideIgdbApi(retrofit: Retrofit): IgdbApi {
        return retrofit.create(IgdbApi::class.java)
    }

    /**
     * Proporciona la implementación del repositorio de juegos.
     *
     * Responsabilidades:
     * - Construir queries en formato IGDB
     * - Mapear DTOs a modelos de dominio
     * - Manejar errores de la API
     */
    @Provides
    @Singleton
    fun provideGameRepository(igdbApi: IgdbApi, gameCacheDao: com.mariolos27.gamerdex.data.datasource.local.dao.GameCacheDao): GameRepository {
        return GameRepositoryImpl(igdbApi, gameCacheDao)
    }

    /**
     * Proporciona la implementación del repositorio de registros de juegos de usuario.
     */
    @Provides
    @Singleton
    fun provideUserGameRepository(firestore: FirebaseFirestore): UserGameRepository {
        return UserGameRepositoryImpl(firestore)
    }
}

