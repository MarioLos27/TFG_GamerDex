package com.mariolos27.gamerdex.presentation.screens.library

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.ui.graphics.Color
import com.mariolos27.gamerdex.R
import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.domain.repository.AuthRepository
import com.mariolos27.gamerdex.domain.usecase.GetAllUserGamesUseCase
import com.mariolos27.gamerdex.presentation.screens.library.model.GameLibraryItem
import com.mariolos27.gamerdex.presentation.screens.library.model.LibraryCategory
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

sealed class LibraryUiState {
    object Loading : LibraryUiState()
    data class Success(
        val items: List<GameLibraryItem>,
        val categories: List<LibraryCategory>
    ) : LibraryUiState()
    data class Error(val message: String) : LibraryUiState()
}

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val getAllUserGamesUseCase: GetAllUserGamesUseCase,
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _selectedCategory = MutableStateFlow(0)
    val selectedCategory: StateFlow<Int> = _selectedCategory.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    val uiState: StateFlow<LibraryUiState> = authRepository.getCurrentUser()
        .flatMapLatest { user ->
            val userId = user?.id
            if (userId != null) {
                combine(
                    getAllUserGamesUseCase(),
                    _selectedCategory,
                    _searchQuery
                ) { allGames, categoryId, query ->
                    val playingCount = allGames.count { it.status == GameStatus.PLAYING }
                    val backlogCount = allGames.count { it.status == GameStatus.ABANDONED || it.status == GameStatus.DROPPED }
                    val completedCount = allGames.count { it.status == GameStatus.COMPLETED }
                    val wishlistCount = allGames.count { it.status == GameStatus.WISHLIST }

                    val categories = listOf(
                        LibraryCategory(0, "Currently Playing", playingCount, R.drawable.ic_currently_playing, Color(0xFFB76DFF), categoryId == 0),
                        LibraryCategory(1, "Backlog", backlogCount, R.drawable.ic_backlog, Color(0xFF94A3B8), categoryId == 1),
                        LibraryCategory(2, "Completed", completedCount, R.drawable.ic_completed, Color(0xFF4EDEA3), categoryId == 2),
                        LibraryCategory(3, "Wishlist", wishlistCount, R.drawable.ic_wishlist, Color(0xFFC4C1FB), categoryId == 3)
                    )

                    val statusFilter = when (categoryId) {
                        0 -> listOf(GameStatus.PLAYING)
                        1 -> listOf(GameStatus.ABANDONED, GameStatus.DROPPED)
                        2 -> listOf(GameStatus.COMPLETED)
                        3 -> listOf(GameStatus.WISHLIST)
                        else -> listOf(GameStatus.PLAYING)
                    }

                    val filteredGames = allGames.filter { game ->
                        game.status in statusFilter &&
                        (query.isBlank() || game.gameTitle.contains(query, ignoreCase = true))
                    }

                    val mappedItems = filteredGames.map { game ->
                        GameLibraryItem(
                            id = game.gameId,
                            title = game.gameTitle,
                            platform = game.platform ?: "Unknown",
                            imageUrl = game.gameCoverUrl ?: "",
                            progress = if (game.status == GameStatus.COMPLETED) 100 else if (game.status == GameStatus.PLAYING) 50 else 0,
                            totalHours = game.hoursPlayed?.toFloat() ?: 0f,
                            status = game.status
                        )
                    }
                    
                    LibraryUiState.Success(mappedItems, categories)
                }
            } else {
                flowOf(LibraryUiState.Error("User not authenticated"))
            }
        }.stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = LibraryUiState.Loading
        )

    fun onCategorySelected(index: Int) {
        _selectedCategory.value = index
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
    }

    fun searchGames() {
        // Local filtering is already reactive based on searchQuery
    }
}
