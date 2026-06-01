package com.mariolos27.gamerdex.presentation.screens.library.components

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.mariolos27.gamerdex.presentation.screens.library.model.GameLibraryItem
import com.mariolos27.gamerdex.presentation.screens.library.model.LibraryCategory
import com.mariolos27.gamerdex.presentation.screens.library.LibraryUiState

@Composable
fun LibraryContent(
    uiState: LibraryUiState,
    categories: List<LibraryCategory>,
    selectedCategory: Int,
    onCategoryClick: (Int) -> Unit,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyVerticalGrid(
        columns = GridCells.Adaptive(minSize = 160.dp),
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Category buttons section
        item(span = { GridItemSpan(maxCurrentLineSpan) }) {
            CategoryGrid(
                categories = categories,
                selectedCategory = selectedCategory,
                onCategoryClick = onCategoryClick
            )
        }

        // Content based on UI state
        when (uiState) {
            is LibraryUiState.Success -> {
                val games = uiState.items
                if (games.isNotEmpty()) {
                    items(games) { game ->
                        GameLibraryCard(
                            game = game,
                            onGameClick = onGameClick,
                            modifier = Modifier.height(320.dp)
                        )
                    }
                } else {
                    item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                        EmptyStateContent(message = "No games found")
                    }
                }
            }
            is LibraryUiState.Loading -> {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator(color = Color(0xFFB76DFF))
                    }
                }
            }
            is LibraryUiState.Error -> {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            uiState.message,
                            color = Color(0xFFFFB4AB),
                            fontSize = 14.sp
                        )
                    }
                }
            }
            else -> {
                item(span = { GridItemSpan(maxCurrentLineSpan) }) {
                    EmptyStateContent(
                        title = "Your library is empty",
                        subtitle = "Search for games to add to your library"
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyStateContent(
    message: String = "",
    title: String = "",
    subtitle: String = "",
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(200.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxSize()
        ) {
            if (message.isNotEmpty()) {
                Text(
                    message,
                    color = Color(0xFF8B949E),
                    fontSize = 16.sp
                )
            } else {
                Text(
                    "🎮",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 12.dp)
                )
                Text(
                    title,
                    color = Color(0xFF8B949E),
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
                Text(
                    subtitle,
                    color = Color(0xFF8B949E),
                    fontSize = 14.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

