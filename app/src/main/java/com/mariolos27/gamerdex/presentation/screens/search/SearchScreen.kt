package com.mariolos27.gamerdex.presentation.screens.search

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariolos27.gamerdex.presentation.components.*
import com.mariolos27.gamerdex.presentation.navigation.Screen
import com.mariolos27.gamerdex.presentation.theme.DarkBackground
import com.mariolos27.gamerdex.presentation.screens.search.model.SearchUiState

@Composable
fun SearchScreen(
    onGameClick: (Long) -> Unit,
    onNavigateToHome: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    isUserAuthenticated: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: SearchViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val searchQuery by viewModel.searchQuery.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 70.dp) // padding para la barra de navegación
        ) {
            when (uiState) {
                is SearchUiState.Idle -> {
                    IdleSearchContent(
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }
                is SearchUiState.Loading -> {
                    LoadingSearchContent(
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }
                is SearchUiState.Success -> {
                    SuccessSearchContent(
                        games = (uiState as SearchUiState.Success).games,
                        onGameClick = onGameClick,
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() }
                    )
                }
                is SearchUiState.Error -> {
                    ErrorSearchContent(
                        message = (uiState as SearchUiState.Error).message,
                        searchQuery = searchQuery,
                        onQueryChanged = { viewModel.onSearchQueryChanged(it) },
                        onSearch = { viewModel.searchGames() },
                        onClear = { viewModel.clearSearch() },
                        onRetry = { viewModel.searchGames() }
                    )
                }
            }
        }

        // Bottom Navigation Bar
        GamerBottomNavigation(
            currentRoute = Screen.Search.route,
            onNavigateToHome = onNavigateToHome,
            onNavigateToLibrary = onNavigateToLibrary,
            onNavigateToProfile = onNavigateToProfile,
            isUserAuthenticated = isUserAuthenticated,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

@Composable
private fun IdleSearchContent(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(16.dp)
        )
        IdleStateContent(modifier = Modifier.fillMaxSize())
    }
}

@Composable
private fun LoadingSearchContent(
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(16.dp)
        )
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
    }
}

@Composable
private fun SuccessSearchContent(
    games: List<com.mariolos27.gamerdex.domain.model.Game>,
    onGameClick: (Long) -> Unit,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(16.dp)
        )

        if (games.isEmpty()) {
            NoResultsStateContent(query = searchQuery, modifier = Modifier.fillMaxSize())
        } else {
            LazyColumn(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games) { game ->
                    GameCard(game = game, onClick = { onGameClick(game.id) })
                }
            }
        }
    }
}

@Composable
private fun ErrorSearchContent(
    message: String,
    searchQuery: String,
    onQueryChanged: (String) -> Unit,
    onSearch: () -> Unit,
    onClear: () -> Unit,
    onRetry: () -> Unit
) {
    Column(modifier = Modifier.fillMaxSize()) {
        GamerSearchField(
            query = searchQuery,
            onQueryChanged = onQueryChanged,
            onSearch = onSearch,
            onClear = onClear,
            modifier = Modifier.padding(16.dp)
        )
        ErrorStateContent(message = message, onRetry = onRetry, modifier = Modifier.fillMaxSize())
    }
}
