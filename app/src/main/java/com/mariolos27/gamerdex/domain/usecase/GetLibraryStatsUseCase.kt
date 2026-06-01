package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.GameStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

data class LibraryStats(
    val totalGames: Int = 0,
    val completedGames: Int = 0,
    val averageRating: Float = 0f,
    val totalHours: Int = 0
)

class GetLibraryStatsUseCase @Inject constructor(
    private val getAllUserGamesUseCase: GetAllUserGamesUseCase
) {
    operator fun invoke(): Flow<LibraryStats> {
        return getAllUserGamesUseCase().map { games ->
            val totalGames = games.size
            val completedGames = games.count { it.status == GameStatus.COMPLETED }
            
            val gamesWithRating = games.filter { it.rating != null }
            val averageRating = if (gamesWithRating.isNotEmpty()) {
                gamesWithRating.mapNotNull { it.rating }.average().toFloat()
            } else {
                0f
            }
            
            val totalHours = games.sumOf { it.hoursPlayed ?: 0 }
            
            LibraryStats(
                totalGames = totalGames,
                completedGames = completedGames,
                averageRating = averageRating,
                totalHours = totalHours
            )
        }
    }
}
