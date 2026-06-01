package com.mariolos27.gamerdex.presentation.screens.game

import android.util.Log
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.usecase.GetGameDetailUseCase
import com.mariolos27.gamerdex.domain.usecase.GetUserGameLogFlowUseCase
import com.mariolos27.gamerdex.domain.usecase.SaveGameLogUseCase
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.model.UserGame
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import kotlinx.coroutines.Job
import javax.inject.Inject
import com.mariolos27.gamerdex.presentation.screens.game.model.GameDetailUiState

/**
 * ViewModel para la pantalla de detalle de juego.
 * 
 * Responsabilidades:
 * - Obtener el ID del juego desde los argumentos de navegación
 * - Cargar los detalles del juego mediante el UseCase
 * - Cargar el registro del usuario (si existe) para este juego
 * - Mantener el estado de la pantalla
 * - Manejar errores y reintentos
 */
@HiltViewModel
class GameDetailViewModel @Inject constructor(
    private val getGameDetailUseCase: GetGameDetailUseCase,
    private val getUserGameLogFlowUseCase: GetUserGameLogFlowUseCase,
    private val saveGameLogUseCase: SaveGameLogUseCase,
    private val authRepository: AuthRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    companion object {
        private const val TAG = "GameDetailViewModel"
        private const val GAME_ID_KEY = "gameId"
    }

    private val _uiState = MutableStateFlow<GameDetailUiState>(GameDetailUiState.Loading)
    val uiState: StateFlow<GameDetailUiState> = _uiState.asStateFlow()

    private val _userLog = MutableStateFlow<UserGame?>(null)
    val userLog: StateFlow<UserGame?> = _userLog.asStateFlow()

    private val gameId: Long = savedStateHandle.get<Long>(GAME_ID_KEY) ?: 0L
    private var logJob: Job? = null

    init {
        loadData()
    }

    private fun loadData() {
        loadGameDetail()
        loadUserLog()
    }

    /**
     * Carga los detalles del juego desde la API
     */
    private fun loadGameDetail() {
        Log.d(TAG, " Iniciando carga de detalles... ID: $gameId")
        
        if (gameId <= 0) {
            Log.e(TAG, "❌ ID de juego inválido: $gameId")
            _uiState.value = GameDetailUiState.Error("ID de juego inválido")
            return
        }

        viewModelScope.launch {
            _uiState.value = GameDetailUiState.Loading
            
            try {
                Log.d(TAG, " Llamando a GetGameDetailUseCase...")
                val result = getGameDetailUseCase(gameId)

                result.onSuccess { game ->
                    Log.d(TAG, "✅ Juego cargado: ${game.title}")
                    _uiState.value = GameDetailUiState.Success(game)
                }

                result.onFailure { exception ->
                    Log.e(TAG, "❌ Error al obtener detalles", exception)
                    val errorMessage = exception.message ?: "Error desconocido"
                    _uiState.value = GameDetailUiState.Error(errorMessage)
                }
            } catch (e: Exception) {
                Log.e(TAG, "❌ Excepción en loadGameDetail", e)
                _uiState.value = GameDetailUiState.Error(e.message ?: "Error desconocido")
            }
        }
    }

    private fun loadUserLog() {
        if (gameId <= 0) return
        logJob?.cancel()
        logJob = viewModelScope.launch {
            getUserGameLogFlowUseCase(gameId).collect { result ->
                result.onSuccess { log ->
                    _userLog.value = log
                }.onFailure {
                    Log.e(TAG, "Error getting user log stream", it)
                }
            }
        }
    }

    /**
     * Reintenta cargar los datos
     */
    fun retry() {
        Log.d(TAG, " Reintentando carga...")
        loadData()
    }

    fun refreshUserLog() {
        // Obsoleto, ya que ahora usamos Flow, pero lo mantenemos vacio para compatibilidad
    }

    fun toggleWishlist() {
        viewModelScope.launch {
            val currentUser = authRepository.getCurrentUser().firstOrNull()
            val currentUserId = currentUser?.id
            
            if (currentUserId == null) {
                Log.e(TAG, "User not authenticated")
                return@launch
            }

            val gameUiState = _uiState.value
            if (gameUiState !is GameDetailUiState.Success) return@launch

            val game = gameUiState.game
            val currentLog = _userLog.value
            val newUserGame = if (currentLog != null) {
                if (currentLog.status == GameStatus.WISHLIST) {
                    currentLog.copy(status = GameStatus.PLAYING)
                } else {
                    currentLog.copy(status = GameStatus.WISHLIST)
                }
            } else {
                UserGame(
                    id = "${currentUserId}_${game.id}",
                    userId = currentUserId,
                    gameId = game.id,
                    status = GameStatus.WISHLIST,
                    gameTitle = game.title,
                    gameCoverUrl = game.coverUrl
                )
            }

            val result = saveGameLogUseCase(newUserGame)
            result.onSuccess {
                Log.d(TAG, "Successfully toggled wishlist")
            }.onFailure {
                Log.e(TAG, "Failed to toggle wishlist", it)
            }
        }
    }
}
