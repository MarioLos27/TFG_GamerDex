package com.mariolos27.gamerdex.presentation.screens.library

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import com.mariolos27.gamerdex.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariolos27.gamerdex.presentation.screens.library.components.LibraryContent
import com.mariolos27.gamerdex.presentation.screens.library.components.LibraryHeader
import com.mariolos27.gamerdex.presentation.screens.library.model.LibraryCategory
import com.mariolos27.gamerdex.presentation.components.GamerBottomNavigation
import com.mariolos27.gamerdex.presentation.navigation.Screen
import com.mariolos27.gamerdex.presentation.theme.DarkBackground

@Composable
fun LibraryScreen(
    onGameClick: (Long) -> Unit,
    onAddGameClick: () -> Unit,
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToReviews: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    isUserAuthenticated: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val searchQuery by viewModel.searchQuery.collectAsState()
    val selectedCategory by viewModel.selectedCategory.collectAsState()
    val uiState by viewModel.uiState.collectAsState()

    val displayCategories = (uiState as? LibraryUiState.Success)?.categories ?: listOf(
        LibraryCategory(0, "Currently Playing", 0,R.drawable.ic_currently_playing, Color(0xFFB76DFF), selectedCategory == 0),
        LibraryCategory(1, "Backlog", 0, R.drawable.ic_backlog, Color(0xFF8B949E), selectedCategory == 1),
        LibraryCategory(2, "Completed", 0, R.drawable.ic_completed, Color(0xFF4EDEA3), selectedCategory == 2),
        LibraryCategory(3, "Wishlist", 0, R.drawable.ic_wishlist, Color(0xFFC4C1FB), selectedCategory == 3)
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(DarkBackground)
    ) {
        // Main content
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Header with search
            LibraryHeader(
                searchQuery = searchQuery,
                onSearchQueryChanged = viewModel::onSearchQueryChanged,
                onSearch = viewModel::searchGames
            )

            // Content with grid and games
            LibraryContent(
                uiState = uiState,
                categories = displayCategories,
                selectedCategory = selectedCategory,
                onCategoryClick = viewModel::onCategorySelected,
                onGameClick = onGameClick
            )
        }

        // Bottom Navigation Bar
        GamerBottomNavigation(
            currentRoute = Screen.Library.route,
            onNavigateToHome = onNavigateToDashboard,
            onNavigateToLibrary = { /* Already on library */ },
            onNavigateToProfile = onNavigateToProfile,
            isUserAuthenticated = isUserAuthenticated,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
