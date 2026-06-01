package com.mariolos27.gamerdex.data.api.dto

import com.google.gson.annotations.SerializedName
import java.time.Instant
import java.time.ZoneOffset

/**
 * DTO extendido para obtener detalles completos de un juego desde IGDB.
 * 
 * Query IGDB:
 * fields id, name, summary, cover.image_id, rating, first_release_date,
 *        genres.name, platforms.name, involved_companies.company.name,
 *        screenshots.image_id, aggregated_rating, rating_count;
 * where id = {igdbId};
 * 
 * Ejemplo de respuesta JSON:
 *
 * {
 *   "id": 1020,
 *   "name": "The Witcher 3: Wild Hunt",
 *   "summary": "...",
 *   "cover": { "image_id": "co2k48" },
 *   "rating": 4.2,
 *   "first_release_date": 1463529600,
 *   "genres": [{ "name": "RPG" }],
 *   "platforms": [{ "name": "PC" }],
 *   "involved_companies": [{ "company": { "name": "CD Projekt Red" } }],
 *   "screenshots": [{ "image_id": "ss123" }],
 *   "aggregated_rating": 92.5,
 *   "rating_count": 1500
 * }
 */
data class GameDetailDto(
    val id: Long,
    val name: String,
    val summary: String? = null,
    val cover: CoverDto? = null,
    val rating: Float? = null,
    @SerializedName("first_release_date")
    val firstReleaseDate: Long? = null,
    val genres: List<GenreDto>? = null,
    val platforms: List<PlatformDto>? = null,
    @SerializedName("involved_companies")
    val involvedCompanies: List<InvolvedCompanyDto>? = null,
    val screenshots: List<ScreenshotDto>? = null,
    @SerializedName("aggregated_rating")
    val aggregatedRating: Float? = null,
    @SerializedName("rating_count")
    val ratingCount: Long? = null
)

data class GenreDto(
    val name: String
)

data class PlatformDto(
    val name: String
)

data class InvolvedCompanyDto(
    val company: CompanyDto? = null
)

data class CompanyDto(
    val name: String
)

data class ScreenshotDto(
    @SerializedName("image_id")
    val imageId: String? = null
) {
    fun getImageUrl(): String? {
        return imageId?.let { id ->
            "https://images.igdb.com/igdb/image/upload/t_screenshot_big/$id.jpg"
        }
    }
}

/**
 * Extensión para convertir timestamp UNIX a año
 */
fun Long?.toYear(): Int? {
    if (this == null) return null
    return try {
        val instant = Instant.ofEpochSecond(this)
        instant.atZone(ZoneOffset.UTC).year
    } catch (e: Exception) {
        null
    }
}

/**
 * Extensión para construir URL de cobertura
 */
fun CoverDto?.getImageUrl(sizeTemplate: String = "t_cover_big"): String? {
    return this?.imageId?.let { id ->
        "https://images.igdb.com/igdb/image/upload/$sizeTemplate/$id.jpg"
    }
}
