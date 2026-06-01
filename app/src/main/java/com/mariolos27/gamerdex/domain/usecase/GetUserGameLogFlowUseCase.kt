package com.mariolos27.gamerdex.domain.usecase

import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetUserGameLogFlowUseCase @Inject constructor(
    private val repository: UserGameRepository,
    private val authRepository: AuthRepository
) {
    operator fun invoke(gameId: Long): Flow<Result<UserGame?>> {
        return authRepository.getCurrentUser().flatMapLatest { user ->
            val userId = user?.id
            if (userId != null) {
                repository.getUserGameFlow(userId, gameId)
            } else {
                flowOf(Result.failure(Exception("User not authenticated")))
            }
        }
    }
}
