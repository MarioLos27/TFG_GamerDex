package com.mariolos27.gamerdex.data.datasource.local.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.mariolos27.gamerdex.data.datasource.local.entity.GameCacheEntity

@Dao
interface GameCacheDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(games: List<GameCacheEntity>)

    @Query("SELECT * FROM games_cache WHERE title LIKE '%' || :query || '%'")
    suspend fun search(query: String): List<GameCacheEntity>
}
