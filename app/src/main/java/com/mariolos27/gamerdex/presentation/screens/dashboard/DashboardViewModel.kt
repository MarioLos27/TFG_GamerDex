package com.mariolos27.gamerdex.presentation.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.repository.GameRepository
import com.mariolos27.gamerdex.presentation.screens.dashboard.model.DashboardUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.async
import javax.inject.Inject

@HiltViewModel
class DashboardViewModel @Inject constructor(
    private val repository: GameRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
        
        viewModelScope.launch {
            try {
                // Fetch concurrently using async to drastically reduce load times
                val trendingDeferred = async { repository.getTrendingGames() }
                val newReleasesDeferred = async { repository.getNewReleases() }
                val comingSoonDeferred = async { repository.getComingSoonGames() }
                val topRatedDeferred = async { repository.getTopRatedGames() }

                val trendingResult = trendingDeferred.await()
                val newReleasesResult = newReleasesDeferred.await()
                val comingSoonResult = comingSoonDeferred.await()
                val topRatedResult = topRatedDeferred.await()

                if (trendingResult.isSuccess && newReleasesResult.isSuccess && 
                    comingSoonResult.isSuccess && topRatedResult.isSuccess) {
                    
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        trendingGames = trendingResult.getOrDefault(emptyList()),
                        newReleases = newReleasesResult.getOrDefault(emptyList()),
                        comingSoonGames = comingSoonResult.getOrDefault(emptyList()),
                        topRatedGames = topRatedResult.getOrDefault(emptyList())
                    )
                } else {
                    _uiState.value = _uiState.value.copy(
                        isLoading = false,
                        errorMessage = "Error loading dashboard data. Check your connection or token."
                    )
                }
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.message ?: "Unknown error"
                )
            }
        }
    }
}
