package com.mariolos27.gamerdex.presentation.screens.game

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.firebase.auth.FirebaseAuth
import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.usecase.DeleteGameLogUseCase
import com.mariolos27.gamerdex.domain.usecase.GetUserGameLogUseCase
import com.mariolos27.gamerdex.domain.usecase.SaveGameLogUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mariolos27.gamerdex.presentation.screens.game.model.GameLogUiState

@HiltViewModel
class GameLogViewModel @Inject constructor(
    private val getUserGameLogUseCase: GetUserGameLogUseCase,
    private val saveGameLogUseCase: SaveGameLogUseCase,
    private val deleteGameLogUseCase: DeleteGameLogUseCase,
    private val firebaseAuth: FirebaseAuth
) : ViewModel() {

    private val _uiState = MutableStateFlow<GameLogUiState>(GameLogUiState.Idle)
    val uiState: StateFlow<GameLogUiState> = _uiState.asStateFlow()

    private val _status = MutableStateFlow(GameStatus.WISHLIST)
    val status = _status.asStateFlow()

    private val _rating = MutableStateFlow<Int?>(null)
    val rating = _rating.asStateFlow()

    private val _review = MutableStateFlow("")
    val review = _review.asStateFlow()

    private val _hoursPlayed = MutableStateFlow<Int?>(null)
    val hoursPlayed = _hoursPlayed.asStateFlow()

    private val _platform = MutableStateFlow<String?>(null)
    val platform = _platform.asStateFlow()

    private val _isFavorite = MutableStateFlow(false)
    val isFavorite = _isFavorite.asStateFlow()

    fun loadUserGame(gameId: Long) {
        viewModelScope.launch {
            _uiState.value = GameLogUiState.Loading
            getUserGameLogUseCase(gameId).onSuccess { userGame ->
                userGame?.let {
                    _status.value = it.status
                    _rating.value = it.rating
                    _review.value = it.review ?: ""
                    _hoursPlayed.value = it.hoursPlayed
                    _platform.value = it.platform
                    _isFavorite.value = it.isFavorite
                }
                _uiState.value = GameLogUiState.Success(userGame)
            }.onFailure {
                _uiState.value = GameLogUiState.Error(it.message ?: "Error al cargar datos")
            }
        }
    }

    fun updateStatus(newStatus: GameStatus) {
        _status.value = newStatus
    }

    fun updateRating(newRating: Int?) {
        _rating.value = newRating
    }

    fun updateReview(newReview: String) {
        _review.value = newReview
    }

    fun updateHours(hours: Int?) {
        _hoursPlayed.value = hours
    }

    fun updatePlatform(platform: String?) {
        _platform.value = platform
    }

    fun toggleFavorite() {
        _isFavorite.value = !_isFavorite.value
    }

    fun saveLog(gameId: Long, gameTitle: String, gameCoverUrl: String?) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        
        val userGame = UserGame(
            id = "${userId}_$gameId",
            userId = userId,
            gameId = gameId,
            status = _status.value,
            rating = _rating.value,
            review = _review.value.takeIf { it.isNotBlank() },
            hoursPlayed = _hoursPlayed.asStateFlow().value,
            platform = _platform.value,
            gameTitle = gameTitle,
            gameCoverUrl = gameCoverUrl,
            isFavorite = _isFavorite.value,
            lastUpdated = System.currentTimeMillis()
        )

        viewModelScope.launch {
            _uiState.value = GameLogUiState.Loading
            saveGameLogUseCase(userGame).onSuccess {
                _uiState.value = GameLogUiState.Saved
            }.onFailure {
                _uiState.value = GameLogUiState.Error(it.message ?: "Error al guardar")
            }
        }
    }

    fun deleteLog(gameId: Long) {
        val userId = firebaseAuth.currentUser?.uid ?: return
        
        viewModelScope.launch {
            _uiState.value = GameLogUiState.Loading
            deleteGameLogUseCase(userId, gameId).onSuccess {
                _uiState.value = GameLogUiState.Deleted
            }.onFailure {
                _uiState.value = GameLogUiState.Error(it.message ?: "Error al eliminar")
            }
        }
    }
}
