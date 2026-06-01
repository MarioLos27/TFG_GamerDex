package com.mariolos27.gamerdex.data.mapper

import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.model.UserGame

/**
 * Mapeador para convertir entre los datos de Firestore y el modelo de dominio UserGame.
 */
object UserGameMapper {

    fun toDomain(id: String, data: Map<String, Any?>): UserGame {
        return UserGame(
            id = id,
            userId = data["userId"] as? String ?: "",
            gameId = (data["gameId"] as? Long) ?: (data["gameId"] as? Int)?.toLong() ?: 0L,
            status = try {
                GameStatus.valueOf(data["status"] as? String ?: GameStatus.WISHLIST.name)
            } catch (e: Exception) {
                GameStatus.WISHLIST
            },
            rating = (data["rating"] as? Long)?.toInt() ?: (data["rating"] as? Int),
            review = data["review"] as? String,
            hoursPlayed = (data["hoursPlayed"] as? Long)?.toInt() ?: (data["hoursPlayed"] as? Int),
            platform = data["platform"] as? String,
            startDate = data["startDate"] as? Long,
            completionDate = data["completionDate"] as? Long,
            lastUpdated = data["lastUpdated"] as? Long ?: System.currentTimeMillis(),
            gameTitle = data["gameTitle"] as? String ?: "",
            gameCoverUrl = data["gameCoverUrl"] as? String,
            isFavorite = data["isFavorite"] as? Boolean ?: false
        )
    }

    fun toFirestore(userGame: UserGame): Map<String, Any?> {
        return mapOf(
            "userId" to userGame.userId,
            "gameId" to userGame.gameId,
            "status" to userGame.status.name,
            "rating" to userGame.rating,
            "review" to userGame.review,
            "hoursPlayed" to userGame.hoursPlayed,
            "platform" to userGame.platform,
            "startDate" to userGame.startDate,
            "completionDate" to userGame.completionDate,
            "lastUpdated" to userGame.lastUpdated,
            "gameTitle" to userGame.gameTitle,
            "gameCoverUrl" to userGame.gameCoverUrl,
            "isFavorite" to userGame.isFavorite
        )
    }
}
