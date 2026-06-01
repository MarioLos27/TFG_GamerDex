package com.mariolos27.gamerdex.data.datasource.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.mariolos27.gamerdex.data.datasource.local.dao.GameCacheDao
import com.mariolos27.gamerdex.data.datasource.local.entity.GameCacheEntity

@Database(entities = [GameCacheEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun gameCacheDao(): GameCacheDao
}
