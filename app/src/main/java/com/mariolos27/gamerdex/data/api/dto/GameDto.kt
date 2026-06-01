package com.mariolos27.gamerdex.data.api.dto

import com.google.gson.annotations.SerializedName
import com.mariolos27.gamerdex.domain.model.Game

/**
 * Data Transfer Object que mapea la respuesta de IGDB.
 * 
 * IGDB devuelve la estructura de cobertura de forma anidada.
 * Ejemplo JSON:
 * {
 *   "id": 1020,
 *   "name": "The Witcher 3: Wild Hunt",
 *   "cover": {
 *     "image_id": "co2k48"
 *   }
 * }
 */
data class GameDto(
    val id: Long,
    val name: String,
    val cover: CoverDto? = null
) {
    /**
     * Convierte el DTO al modelo de dominio Game.
     * 
     * La URL se construye usando Cloudinary CDN de IGDB:
     * https://images.igdb.com/igdb/image/upload/t_cover_big/{image_id}.jpg
     */
    fun toDomain(): Game {
        // Si hay cover, usar getImageUrl() que construye la URL correcta
        val imageUrl = cover?.getImageUrl()
        
        return Game(
            id = id,
            title = name,
            coverUrl = imageUrl
        )
    }
}

/**
 * DTO para la cobertura del juego.
 * Mapea la respuesta anidada de IGDB.
 * 
 * Ejemplo:
 * {
 *   "image_id": "co2k48"
 * }
 */
data class CoverDto(
    @SerializedName("image_id")
    val imageId: String? = null,
    val url: String? = null
) {
    /**
     * Construye la URL completa de la imagen usando Cloudinary (CDN de IGDB).
     * 
     * Parámetros de Cloudinary:
     * - t_cover_big: Tamaño grande de portada
     * 
     * URL final: https://images.igdb.com/igdb/image/upload/t_cover_big/{imageId}.jpg
     */
    fun getImageUrl(): String? {
        return imageId?.let { id ->
            "https://images.igdb.com/igdb/image/upload/t_cover_big/$id.jpg"
        }
    }
}


