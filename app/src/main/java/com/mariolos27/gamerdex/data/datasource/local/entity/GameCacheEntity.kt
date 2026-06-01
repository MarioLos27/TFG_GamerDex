package com.mariolos27.gamerdex.data.datasource.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.mariolos27.gamerdex.domain.model.Game

@Entity(tableName = "games_cache")
data class GameCacheEntity(
    @PrimaryKey val igdbId: Long,
    val title: String,
    val coverUrl: String?,
    val rating: Double,
    val cachedAt: Long
) {
    fun toDomain(): Game {
        return Game(
            id = igdbId,
            title = title,
            coverUrl = coverUrl,
            rating = rating.toFloat()
        )
    }
}
