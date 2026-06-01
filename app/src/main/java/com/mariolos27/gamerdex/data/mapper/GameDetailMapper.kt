package com.mariolos27.gamerdex.data.mapper

import com.mariolos27.gamerdex.data.api.dto.GameDetailDto
import com.mariolos27.gamerdex.data.api.dto.toYear
import com.mariolos27.gamerdex.data.api.dto.getImageUrl
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.model.Platform

/**
 * Mapea un GameDetailDto a un modelo de dominio Game.
 * Extrae los campos relevantes y construye las URLs de imágenes correctamente.
 */
object GameDetailMapper {
    
    fun mapToDomain(dto: GameDetailDto): Game {
        return Game(
            id = dto.id,
            title = dto.name,
            coverUrl = dto.cover?.getImageUrl(),
            year = dto.firstReleaseDate?.toYear(),
            developer = dto.involvedCompanies
                ?.firstOrNull { it.company != null }
                ?.company?.name,
            description = dto.summary,
            rating = (dto.aggregatedRating ?: (dto.rating?.times(10) ?: 0f)) / 10,
            fans = formatCount(dto.ratingCount ?: 0),
            reviews = "0",  // IGDB no proporciona review count, usar 0 o dato alternativo
            genres = dto.genres?.map { it.name } ?: emptyList(),
            platforms = dto.platforms?.map { 
                Platform(
                    name = it.name,
                    iconUrl = ""  // IGDB no proporciona URLs de iconos
                )
            } ?: emptyList(),
            backdropUrl = dto.screenshots?.firstOrNull()?.getImageUrl()
        )
    }
    
    /**
     * Formatea números grandes (1500 -> "1.5k")
     */
    private fun formatCount(count: Long): String {
        return when {
            count >= 1_000_000 -> "${(count / 1_000_000f).toInt()}M"
            count >= 1_000 -> "${(count / 1_000f)}k"
            else -> count.toString()
        }
    }
}
