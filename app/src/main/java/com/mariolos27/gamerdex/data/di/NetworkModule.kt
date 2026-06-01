package com.mariolos27.gamerdex.data.di

import com.mariolos27.gamerdex.BuildConfig
import android.util.Log
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import okhttp3.Interceptor
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import okio.Buffer
import javax.inject.Singleton

/**
 * Módulo Hilt para configuración de la red HTTP.
 *
 * Configuración:
 * - OkHttpClient con interceptores de autenticación
 * - Headers requeridos por IGDB API
 * - URL base para Retrofit
 *
 * Credenciales requeridas:
 * - Client-ID: Obtenido de Twitch Developer Console
 * - Access-Token: Token OAuth de Twitch (se obtiene en https://id.twitch.tv/oauth2/token)
 *
 */
@Module
@InstallIn(SingletonComponent::class)
object NetworkModule {

    /**
     * Crear un Interceptor para añadir headers de autenticación a todas las requests
     */
    @Provides
    @Singleton
    fun provideAuthInterceptor(): Interceptor {
        return Interceptor { chain ->
            val originalRequest = chain.request()

            // Headers requeridos por IGDB API
            val requestBuilder = originalRequest.newBuilder()
                .addHeader("Client-ID", BuildConfig.IGDB_CLIENT_ID)
                .addHeader("Authorization", "Bearer ${BuildConfig.IGDB_ACCESS_TOKEN}")
                // Asegurar que Content-Type sea text/plain para las queries de IGDB
                .header("Content-Type", "text/plain")

            val newRequest = requestBuilder.build()

            Log.d("NetworkModule", "📤 Request URL: ${newRequest.url}")
            Log.d("NetworkModule", "📤 Request Method: ${newRequest.method}")
            Log.d("NetworkModule", "📤 Headers: Client-ID, Authorization, Content-Type: text/plain")
            
            // Mostrar el body si existe
            if (newRequest.body != null) {
                try {
                    val buffer = Buffer()
                    newRequest.body!!.writeTo(buffer)
                    Log.d("NetworkModule", "📤 Body: ${buffer.readUtf8()}")
                } catch (e: Exception) {
                    Log.d("NetworkModule", "📤 Body: (could not read)")
                }
            }
            
            // Procesar la respuesta y capturar detalles importantes
            val response = chain.proceed(newRequest)
            
            Log.d("NetworkModule", "📥 Response Code: ${response.code}")
            Log.d("NetworkModule", "📥 Response Message: ${response.message}")
            
            if (!response.isSuccessful) {
                Log.e("NetworkModule", "❌ API ERROR - Code: ${response.code}")
                try {
                    val errorBody = response.peekBody(Long.MAX_VALUE).string()
                    Log.e("NetworkModule", "❌ Error Body: $errorBody")
                } catch (e: Exception) {
                    Log.e("NetworkModule", "❌ Could not read error body: ${e.message}")
                }
            } else {
                // Log del body de la respuesta para debugging
                try {
                    val responseBody = response.peekBody(Long.MAX_VALUE).string()
                    Log.d("NetworkModule", "📥 Response Body (primeros 500 chars): ${responseBody.take(500)}")
                } catch (e: Exception) {
                    Log.d("NetworkModule", "📥 Could not read response body: ${e.message}")
                }
            }
            
            response
        }
    }

    /**
     * HttpLoggingInterceptor para ver las requests y responses completas
     */
    @Provides
    @Singleton
    fun provideLoggingInterceptor(): HttpLoggingInterceptor {
        val loggingInterceptor = HttpLoggingInterceptor { message ->
            Log.d("OkHttp", message)
        }
        loggingInterceptor.level = HttpLoggingInterceptor.Level.BODY
        return loggingInterceptor
    }

    /**
     * Proporciona un cliente HTTP configurado con interceptores
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(
        authInterceptor: Interceptor,
        loggingInterceptor: HttpLoggingInterceptor
    ): OkHttpClient {
        return OkHttpClient.Builder()
            .addInterceptor(authInterceptor)  // Primero: añadir auth headers
            .addInterceptor(loggingInterceptor)  // Segundo: log de requests/responses
            .build()
    }

    /**
     * Proporciona una instancia de Gson configurada para ser permisiva con IGDB
     */
    @Provides
    @Singleton
    fun provideGson(): Gson {
        return GsonBuilder()
            .setLenient()  // Permite JSON no estrictamente válido
            .create()
    }

    /**
     * Proporciona una instancia de Retrofit configurada para IGDB.
     * Usa el OkHttpClient con autenticación.
     */
    @Provides
    @Singleton
    fun provideRetrofit(okHttpClient: OkHttpClient, gson: Gson): Retrofit {
        return Retrofit.Builder()
            .baseUrl("https://api.igdb.com/v4/")
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
}

