package com.mariolos27.gamerdex.data.di

import android.content.Context
import androidx.room.Room
import com.mariolos27.gamerdex.data.datasource.local.AppDatabase
import com.mariolos27.gamerdex.data.datasource.local.dao.GameCacheDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "gamerdex_db"
        ).build()
    }

    @Provides
    @Singleton
    fun provideGameCacheDao(database: AppDatabase): GameCacheDao {
        return database.gameCacheDao()
    }
}
