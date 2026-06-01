package com.mariolos27.gamerdex.presentation.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mariolos27.gamerdex.R
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.presentation.components.GamerBottomNavigation
import com.mariolos27.gamerdex.presentation.navigation.Screen
import com.mariolos27.gamerdex.presentation.theme.DarkBackground
import com.mariolos27.gamerdex.presentation.screens.dashboard.components.DashboardHeader
import com.mariolos27.gamerdex.presentation.screens.dashboard.components.SectionHeader
import com.mariolos27.gamerdex.presentation.screens.dashboard.components.GamesCarousel

@Composable
fun DashboardScreen(
    onGameClick: (Long) -> Unit,
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToSearch: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    isUserAuthenticated: Boolean = false,
    modifier: Modifier = Modifier,
    viewModel: DashboardViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFF0F1115)) // Sleek dark aesthetic
    ) {
        if (uiState.isLoading) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF6B4EE6))
            }
        } else if (uiState.errorMessage != null) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(
                        text = uiState.errorMessage ?: "Unknown error",
                        color = Color.White,
                        fontSize = 16.sp
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(
                        onClick = { viewModel.loadDashboardData() },
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF6B4EE6))
                    ) {
                        Text("Retry", color = Color.White)
                    }
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(bottom = 80.dp), // Padding for BottomNav
                contentPadding = PaddingValues(bottom = 24.dp)
            ) {
                item {
                    DashboardHeader()
                }

                if (uiState.topRatedGames.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Top Rated Games")
                        GamesCarousel(games = uiState.topRatedGames, onGameClick = onGameClick)
                    }
                }

                if (uiState.trendingGames.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Trending Now")
                        GamesCarousel(games = uiState.trendingGames, onGameClick = onGameClick)
                    }
                }

                if (uiState.newReleases.isNotEmpty()) {
                    item {
                        SectionHeader(title = "New Releases")
                        GamesCarousel(games = uiState.newReleases, onGameClick = onGameClick)
                    }
                }

                if (uiState.comingSoonGames.isNotEmpty()) {
                    item {
                        SectionHeader(title = "Coming Soon")
                        GamesCarousel(games = uiState.comingSoonGames, onGameClick = onGameClick)
                    }
                }
            }
        }

        // Bottom Navigation Bar
        GamerBottomNavigation(
            currentRoute = Screen.Dashboard.route,
            onNavigateToLibrary = onNavigateToLibrary,
            onNavigateToProfile = onNavigateToProfile,
            isUserAuthenticated = isUserAuthenticated,
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}
