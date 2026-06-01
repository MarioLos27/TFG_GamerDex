package com.mariolos27.gamerdex.data.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import com.mariolos27.gamerdex.data.mapper.UserGameMapper
import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.repository.UserGameRepository
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

private const val TAG = "UserGameRepoImpl"
private const val COLLECTION_NAME = "user_games"

class UserGameRepositoryImpl @Inject constructor(
    private val firestore: FirebaseFirestore
) : UserGameRepository {

    override suspend fun saveGameLog(userGame: UserGame): Result<Unit> {
        return try {
            val docId = "${userGame.userId}_${userGame.gameId}"
            val data = UserGameMapper.toFirestore(userGame)
            
            firestore.collection(COLLECTION_NAME)
                .document(docId)
                .set(data, SetOptions.merge())
                .await()
            
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error saving game log", e)
            Result.failure(e)
        }
    }

    override suspend fun getUserGame(userId: String, gameId: Long): Result<UserGame?> {
        return try {
            val docId = "${userId}_$gameId"
            val snapshot = firestore.collection(COLLECTION_NAME)
                .document(docId)
                .get()
                .await()
            
            if (snapshot.exists()) {
                val userGame = UserGameMapper.toDomain(snapshot.id, snapshot.data ?: emptyMap())
                Result.success(userGame)
            } else {
                Result.success(null)
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting user game", e)
            Result.failure(e)
        }
    }

    override fun getAllUserGames(userId: String): Flow<List<UserGame>> = callbackFlow {
        val registration = firestore.collection(COLLECTION_NAME)
            .whereEqualTo("userId", userId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    close(error)
                    return@addSnapshotListener
                }
                
                val games = snapshot?.documents?.map { doc ->
                    UserGameMapper.toDomain(doc.id, doc.data ?: emptyMap())
                } ?: emptyList()
                
                trySend(games)
            }
        
        awaitClose { registration.remove() }
    }

    override suspend fun deleteGameLog(userId: String, gameId: Long): Result<Unit> {
        return try {
            val docId = "${userId}_$gameId"
            firestore.collection(COLLECTION_NAME)
                .document(docId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Error deleting game log", e)
            Result.failure(e)
        }
    }

    override fun getUserGameFlow(userId: String, gameId: Long): Flow<Result<UserGame?>> = callbackFlow {
        val docId = "${userId}_$gameId"
        val registration = firestore.collection(COLLECTION_NAME)
            .document(docId)
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    trySend(Result.failure(error))
                    return@addSnapshotListener
                }
                
                if (snapshot != null && snapshot.exists()) {
                    val userGame = UserGameMapper.toDomain(snapshot.id, snapshot.data ?: emptyMap())
                    trySend(Result.success(userGame))
                } else {
                    trySend(Result.success(null))
                }
            }
            
        awaitClose { registration.remove() }
    }
}
