package com.mariolos27.gamerdex.presentation.screens.search

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.usecase.SearchGamesUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.mariolos27.gamerdex.presentation.screens.search.model.SearchUiState

@HiltViewModel
class SearchViewModel @Inject constructor(
    private val searchGamesUseCase: SearchGamesUseCase
) : ViewModel() {
    
    private val _uiState = MutableStateFlow<SearchUiState>(SearchUiState.Idle)
    val uiState: StateFlow<SearchUiState> = _uiState.asStateFlow()
    
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private var searchJob: Job? = null
    
    fun onSearchQueryChanged(newQuery: String) {
        _searchQuery.value = newQuery
        
        // Cancelar la búsqueda anterior si el usuario sigue escribiendo
        searchJob?.cancel()
        
        if (newQuery.isBlank()) {
            _uiState.value = SearchUiState.Idle
            return
        }

        // Debounce: Esperar 500ms antes de buscar
        searchJob = viewModelScope.launch {
            delay(500)
            searchGames()
        }
    }
    
    fun searchGames() {
        val query = _searchQuery.value
        if (query.isBlank()) return
        
        viewModelScope.launch {
            _uiState.value = SearchUiState.Loading
            try {
                val result = searchGamesUseCase(query)
                result.onSuccess { games ->
                    _uiState.value = SearchUiState.Success(games)
                }.onFailure { exception ->
                    _uiState.value = SearchUiState.Error(
                        message = exception.message ?: "Connection error with IGDB",
                        exception = exception
                    )
                }
            } catch (e: Exception) {
                _uiState.value = SearchUiState.Error(
                    message = "Unexpected error: ${e.message}",
                    exception = e
                )
            }
        }
    }
    
    fun clearSearch() {
        _searchQuery.value = ""
        _uiState.value = SearchUiState.Idle
        searchJob?.cancel()
    }
}

