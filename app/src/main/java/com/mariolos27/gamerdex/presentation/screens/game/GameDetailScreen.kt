@file:OptIn(ExperimentalMaterial3Api::class)

package com.mariolos27.gamerdex.presentation.screens.game

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.CheckCircle
import androidx.compose.material.icons.outlined.Favorite
import androidx.compose.material.icons.outlined.PlaylistAdd
import androidx.compose.material.icons.outlined.RateReview
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.mariolos27.gamerdex.R
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.model.Platform
import com.mariolos27.gamerdex.domain.model.UserGame
import com.mariolos27.gamerdex.domain.model.GameStatus
import com.mariolos27.gamerdex.presentation.components.GamerBottomNavigation
import com.mariolos27.gamerdex.presentation.navigation.Screen
import com.mariolos27.gamerdex.presentation.screens.game.model.GameDetailUiState
import com.mariolos27.gamerdex.presentation.screens.game.components.GameLogBottomSheet
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// DATA CLASSES

data class Review(
    val id: String,
    val username: String,
    val avatarUrl: String,
    val rating: Float,
    val reviewCount: String,
    val likeCount: String,
    val text: String,
    val likes: String,
    val comments: String
)

// COLOR PALETTE

private object GamerDexColors {
    val Primary = Color(0xFF7C3AED)
    val PrimaryContainer = Color(0xFFB76DFF)
    val OnPrimary = Color(0xFF490080)
    val Secondary = Color(0xFFC4C1FB)
    val Tertiary = Color(0xFF4EDEA3)
    val TertiaryContainer = Color(0xFF00A572)
    val Surface = Color(0xFF0F1115)
    val SurfaceContainer = Color(0xFF1E2128)
    val SurfaceContainerLow = Color(0xFF1E2128)
    val SurfaceContainerHigh = Color(0xFF2A2D35)
    val SurfaceContainerHighest = Color(0xFF2A2D35)
    val OnBackground = Color(0xFFFFFFFF)
    val OnSurfaceVariant = Color(0xFF8B949E)
    val Background = Color(0xFF0F1115)
    val Error = Color(0xFFFFB4AB)
    val OutlineVariant = Color(0xFF4D4354)
}

// MAIN SCREEN

@Composable
fun GameDetailScreen(
    viewModel: GameDetailViewModel = hiltViewModel(),
    gameId: Long = 0L,
    onBackClick: () -> Unit = {},
    onNotificationsClick: () -> Unit = {},
    onUserAvatarClick: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    userAvatarUrl: String = "https://lh3.googleusercontent.com/aida-public/AB6AXuB2RQjYE_ik56Z9WWJT0aZzALcuiAolx3mhsPUHLx2a8UC0Y2LQzkoAtdhDOzTb5QjbZ7LXdOQ36rWmGLXIvICYajaw7BJY5ayQ2-5pORi39pXOtMx-aSLLFOP35s8yPO9EXg90YlOIo8wVgND755lnH_7JIYK7A5RRqfy8N7bGGUhid86AfuBw5Joyx5xn4waAL3HROMo-5yQqUuWopYVZ5VqdlMJS0igOvUbL9fKvuXE8xjbJbJUhYP-I26bMdvJuzDnBarMooUHO"
) {
    val uiState by viewModel.uiState.collectAsState()
    val userLog by viewModel.userLog.collectAsState()
    var showLogSheet by remember { mutableStateOf(false) }

    GameDetailScreenContent(
        uiState = uiState,
        userLog = userLog,
        onRetry = { viewModel.retry() },
        onBackClick = onBackClick,
        onLogOrReviewClick = { showLogSheet = true },
        onListClick = { showLogSheet = true },
        onPlayedClick = { showLogSheet = true },
        onWishlistClick = { viewModel.toggleWishlist() },
        onNavigateToDashboard = onNavigateToDashboard,
        onNavigateToLibrary = onNavigateToLibrary,
        onNavigateToProfile = onNavigateToProfile
    )

    if (showLogSheet && uiState is GameDetailUiState.Success) {
        GameLogBottomSheet(
            game = (uiState as GameDetailUiState.Success).game,
            onDismiss = { 
                showLogSheet = false
                viewModel.refreshUserLog()
            }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameDetailScreenContent(
    uiState: GameDetailUiState,
    userLog: UserGame?,
    onRetry: () -> Unit,
    onBackClick: () -> Unit = {},
    onLogOrReviewClick: () -> Unit = {},
    onListClick: () -> Unit = {},
    onPlayedClick: () -> Unit = {},
    onWishlistClick: () -> Unit = {},
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {}
) {
    val isWishlisted = userLog?.status == GameStatus.WISHLIST
    
    Scaffold(
        bottomBar = {
            GamerBottomNavigation(
                currentRoute = Screen.Details.route,
                onNavigateToHome = onNavigateToDashboard,
                onNavigateToLibrary = onNavigateToLibrary,
                onNavigateToProfile = onNavigateToProfile,
                isUserAuthenticated = true
            )
        },
        containerColor = GamerDexColors.Background,
        contentColor = GamerDexColors.OnBackground
    ) { paddingValues ->
        Box(modifier = Modifier.fillMaxSize().padding(bottom = paddingValues.calculateBottomPadding())) {
            when (uiState) {
                is GameDetailUiState.Loading -> {
                    LoadingState()
                }
                is GameDetailUiState.Success -> {
                    val game = uiState.game
                    LazyColumn(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(GamerDexColors.Background)
                    ) {
                        item {
                            HeroSection(game = game)
                        }

                        item {
                            ContentArea(
                                game = game,
                                userLog = userLog,
                                reviews = emptyList(), // TODO: Cargar reviews desde API
                                onLogOrReviewClick = onLogOrReviewClick,
                                onListClick = onListClick,
                                onPlayedClick = onPlayedClick
                            )
                        }

                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                        }
                    }
                }
                is GameDetailUiState.Error -> {
                    ErrorState(
                        message = uiState.message,
                        onRetry = onRetry
                    )
                }
            }

            // Icons Overlay
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .statusBarsPadding(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Volver",
                        tint = Color.White
                    )
                }
                
                IconButton(
                    onClick = onWishlistClick,
                    modifier = Modifier.size(40.dp)
                ) {
                    Icon(
                        imageVector = if (isWishlisted) Icons.Filled.Favorite else Icons.Outlined.Favorite,
                        contentDescription = "Wishlist",
                        tint = if (isWishlisted) Color(0xFFDDB7FF) else Color.White
                    )
                }
            }
        }
    }
}

// COMPOSABLE SECTIONS

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun GameDetailTopAppBar(
    onBackClick: () -> Unit,
    onNotificationsClick: () -> Unit,
    onUserAvatarClick: () -> Unit,
    userAvatarUrl: String
) {
    TopAppBar(
        title = {
            Text(
                text = "GamerDex",
                fontSize = 20.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                letterSpacing = (-0.5).sp
            )
        },
        navigationIcon = {
            IconButton(onClick = onBackClick) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Volver",
                    tint = Color.White
                )
            }
        },
        actions = {
            IconButton(onClick = onNotificationsClick) {
                Icon(
                    imageVector = Icons.Default.Notifications,
                    contentDescription = "Notificaciones",
                    tint = Color(0xFF94A3B8)
                )
            }
            IconButton(onClick = onUserAvatarClick) {
                AsyncImage(
                    model = userAvatarUrl,
                    contentDescription = "Avatar de usuario",
                    modifier = Modifier
                        .size(32.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, Color(0xFF6B21A8), RoundedCornerShape(50)),
                    contentScale = ContentScale.Crop
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color(0xFF0F1115).copy(alpha = 0.8f),
            titleContentColor = Color.White,
            navigationIconContentColor = Color.White,
            actionIconContentColor = Color.White
        ),
        modifier = Modifier
            .fillMaxWidth()
            .background(
                brush = Brush.verticalGradient(
                    colors = listOf(
                        Color(0xFF0F1115).copy(alpha = 0.8f),
                        Color(0xFF0F1115).copy(alpha = 0.4f)
                    )
                )
            )
    )
}

@Composable
private fun HeroSection(game: Game) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 400.dp)
            .background(GamerDexColors.Surface)
    ) {
        // Background Image
        AsyncImage(
            model = game.backdropUrl,
            contentDescription = "Fondo del juego",
            modifier = Modifier
                .matchParentSize()
                .blur(radius = 8.dp),
            contentScale = ContentScale.Crop,
            alpha = 0.4f
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .matchParentSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            GamerDexColors.Surface.copy(alpha = 0.6f),
                            GamerDexColors.Surface
                        )
                    )
                )
        )

        // Content
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 24.dp, top = 120.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(20.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                // Poster
                AsyncImage(
                    model = game.coverUrl,
                    contentDescription = "Portada del juego",
                    modifier = Modifier
                        .width(110.dp)
                        .aspectRatio(2f / 3f)
                        .clip(RoundedCornerShape(12.dp))
                        .border(1.dp, Color.White.copy(alpha = 0.2f), RoundedCornerShape(12.dp)),
                    contentScale = ContentScale.Crop
                )

                // Title and Info
                Column(
                    modifier = Modifier
                        .weight(1f)
                        .align(Alignment.Bottom),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    // Genres
                    LazyRow(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(game.genres) { genre ->
                            GenreChip(genre = genre)
                        }
                    }

                    // Title
                    Text(
                        text = game.title,
                        fontSize = 32.sp,
                        fontWeight = FontWeight.Black,
                        color = Color.White,
                        letterSpacing = (-0.5).sp,
                        lineHeight = 36.sp
                    )

                    // Year, Developer
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = game.year?.toString() ?: "",
                            fontSize = 14.sp,
                            color = GamerDexColors.OnSurfaceVariant,
                            fontWeight = FontWeight.Medium
                        )
                        if (game.year != null && game.developer != null) {
                            Box(
                                modifier = Modifier
                                    .size(4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .background(Color(0xFF475569))
                            )
                        }
                        Text(
                            text = game.developer ?: "",
                            fontSize = 14.sp,
                            color = GamerDexColors.OnSurfaceVariant,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun ContentArea(
    game: Game,
    userLog: UserGame?,
    reviews: List<Review>,
    onLogOrReviewClick: () -> Unit,
    onListClick: () -> Unit,
    onPlayedClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp)
            .padding(top = 24.dp),
        verticalArrangement = Arrangement.spacedBy(32.dp)
    ) {
        // Main Stats & Actions
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            StatsCard(game = game, userLog = userLog)
            UserActionsCard(
                userLog = userLog,
                onLogOrReviewClick = onLogOrReviewClick,
                onListClick = onListClick,
                onPlayedClick = onPlayedClick
            )
        }

        // About Section
        AboutSection(game = game)

        // Activity and Details
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(32.dp)
        ) {
            if (userLog != null) {
                YourActivityCard(userLog = userLog)
            }
            
            if (game.platforms.isNotEmpty()) {
                WhereToPlayCard(platforms = game.platforms)
            }
            
            RatingsDistributionCard()
        }

        // Reviews
        PopularReviewsSection(reviews = reviews)
    }
}

@Composable
private fun GenreChip(genre: String) {
    Surface(
        modifier = Modifier
            .clip(RoundedCornerShape(24.dp))
            .border(1.dp, GamerDexColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(24.dp)),
        color = GamerDexColors.Primary.copy(alpha = 0.2f)
    ) {
        Text(
            text = genre.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = GamerDexColors.Primary,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            letterSpacing = 0.5.sp
        )
    }
}

@Composable
private fun StatsCard(game: Game, userLog: UserGame?) {
    val hasPersonalStats = userLog != null
    
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GamerDexColors.SurfaceContainerHigh.copy(alpha = 0.6f)
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Stats Grid
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                StatItem(
                    label = if (hasPersonalStats && userLog?.rating != null) "My Rating" else "Rating",
                    value = (userLog?.rating?.toString() ?: game.rating?.toString()) ?: "0.0",
                    icon = Icons.Default.Star,
                    highlight = hasPersonalStats && userLog?.rating != null
                )
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )
                StatItem(
                    label = if (hasPersonalStats) "Status" else "Fans",
                    value = if (hasPersonalStats) (userLog?.status?.name ?: "UNKNOWN") else (game.fans ?: "0"),
                    icon = null,
                    highlight = hasPersonalStats
                )
                VerticalDivider(
                    modifier = Modifier
                        .width(1.dp)
                        .height(32.dp),
                    color = Color.White.copy(alpha = 0.1f)
                )
                StatItem(
                    label = if (hasPersonalStats) "Hours" else "Reviews",
                    value = if (hasPersonalStats) (userLog?.hoursPlayed?.toString() ?: "-") else (game.reviews ?: "0"),
                    icon = null,
                    highlight = hasPersonalStats
                )
            }
        }
    }
}

@Composable
private fun RowScope.StatItem(label: String, value: String, icon: ImageVector?, highlight: Boolean = false) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.weight(1f)
    ) {
        Text(
            text = label.uppercase(),
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = if (highlight) GamerDexColors.Primary else Color(0xFF64748B),
            letterSpacing = 0.5.sp,
            maxLines = 1
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(top = 4.dp)
        ) {
            Text(
                text = value,
                fontSize = if (value.length > 5) 16.sp else 24.sp,
                fontWeight = FontWeight.Black,
                color = Color.White,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp),
                    tint = if (highlight) GamerDexColors.Primary else GamerDexColors.Tertiary
                )
            }
        }
    }
}

@Composable
private fun UserActionsCard(
    userLog: UserGame?,
    onLogOrReviewClick: () -> Unit,
    onListClick: () -> Unit,
    onPlayedClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onLogOrReviewClick,
            modifier = Modifier
                .fillMaxWidth()
                .height(48.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = GamerDexColors.Primary,
                contentColor = GamerDexColors.OnPrimary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Icon(
                imageVector = Icons.Outlined.RateReview,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (userLog?.review != null) "Edit Review" else "Log or Review",
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            OutlinedButton(
                onClick = onListClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (userLog?.status != null && userLog.status != GameStatus.COMPLETED) 
                        GamerDexColors.Primary.copy(alpha = 0.2f) 
                    else 
                        GamerDexColors.SurfaceContainerHighest,
                    contentColor = if (userLog?.status != null && userLog.status != GameStatus.COMPLETED)
                        GamerDexColors.Primary
                    else
                        Color.White
                ),
                border = BorderStroke(
                    1.dp, 
                    if (userLog?.status != null && userLog.status != GameStatus.COMPLETED)
                        GamerDexColors.Primary.copy(alpha = 0.5f)
                    else
                        Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.PlaylistAdd,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = userLog?.status?.name ?: "List",
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            OutlinedButton(
                onClick = onPlayedClick,
                modifier = Modifier
                    .weight(1f)
                    .height(48.dp),
                colors = ButtonDefaults.outlinedButtonColors(
                    containerColor = if (userLog?.status == GameStatus.COMPLETED)
                        GamerDexColors.Tertiary.copy(alpha = 0.2f)
                    else
                        GamerDexColors.SurfaceContainerHighest,
                    contentColor = if (userLog?.status == GameStatus.COMPLETED)
                        GamerDexColors.Tertiary
                    else
                        Color.White
                ),
                border = BorderStroke(
                    1.dp,
                    if (userLog?.status == GameStatus.COMPLETED)
                        GamerDexColors.Tertiary.copy(alpha = 0.5f)
                    else
                        Color.White.copy(alpha = 0.1f)
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.CheckCircle,
                    contentDescription = null,
                    modifier = Modifier.size(20.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (userLog?.status == GameStatus.COMPLETED) "Played" else "Mark Played",
                    fontWeight = FontWeight.Medium,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Composable
private fun RatingsDistributionCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GamerDexColors.SurfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Ratings Distribution",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp),
                horizontalArrangement = Arrangement.spacedBy(2.dp),
                verticalAlignment = Alignment.Bottom
            ) {
                val heights = listOf(0.1f, 0.15f, 0.2f, 0.35f, 0.5f, 0.7f, 0.9f, 1.0f, 0.85f, 0.6f)
                val colors = listOf(
                    Color(0xFF1E2128), Color(0xFF1E2128), Color(0xFF1E2128), Color(0xFF1E2128),
                    Color(0xFF6B21A8), Color(0xFF7C3AED), Color(0xFF9333EA), Color(0xFFA855F7),
                    Color(0xFFD8B4FE), Color(0xFFE9D5FF)
                )

                heights.forEachIndexed { index, height ->
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .fillMaxHeight(height)
                            .background(
                                color = colors[index],
                                shape = RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp)
                            )
                    )
                }
            }
        }
    }
}

@Composable
private fun WhereToPlayCard(platforms: List<Platform>) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GamerDexColors.SurfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Where to Play",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )

            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                platforms.forEach { platform ->
                    PlatformButton(platform = platform)
                }
            }
        }
    }
}

@Composable
private fun PlatformButton(platform: Platform) {
    OutlinedButton(
        onClick = { },
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp),
        colors = ButtonDefaults.outlinedButtonColors(
            containerColor = Color(0xFF1E2128).copy(alpha = 0.5f),
            contentColor = Color.White
        ),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f)),
        shape = RoundedCornerShape(12.dp)
    ) {
        AsyncImage(
            model = platform.iconUrl,
            contentDescription = platform.name,
            modifier = Modifier.size(20.dp),
            contentScale = ContentScale.Fit
        )
        Spacer(modifier = Modifier.width(12.dp))
        Text(
            text = platform.name,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun YourActivityCard(userLog: UserGame) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GamerDexColors.SurfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "Your Activity",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (userLog.rating != null) {
                    Surface(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(RoundedCornerShape(50)),
                        color = GamerDexColors.Primary.copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, GamerDexColors.Primary.copy(alpha = 0.4f))
                    ) {
                        Box(
                            contentAlignment = Alignment.Center,
                            modifier = Modifier.fillMaxSize()
                        ) {
                            Text(
                                text = userLog.rating.toString(),
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Black,
                                color = GamerDexColors.Primary
                            )
                        }
                    }
                }

                Column(
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    val dateStr = remember(userLog.lastUpdated) {
                        val sdf = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                        sdf.format(Date(userLog.lastUpdated))
                    }
                    Text(
                        text = "${userLog.status.name} on $dateStr",
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = Color.White
                    )
                    if (userLog.hoursPlayed != null) {
                        Text(
                            text = "${userLog.hoursPlayed} hours played",
                            fontSize = 12.sp,
                            color = Color(0xFF64748B)
                        )
                    }
                }
            }

            if (!userLog.review.isNullOrBlank()) {
                Text(
                    text = userLog.review,
                    fontSize = 14.sp,
                    lineHeight = 20.sp,
                    color = Color(0xFFCBD5E1),
                    modifier = Modifier.padding(top = 8.dp)
                )
            }
        }
    }
}

@Composable
private fun AboutSection(game: Game) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "About",
            fontSize = 10.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFF64748B),
            letterSpacing = 1.sp
        )
        Text(
            text = game.description ?: "",
            fontSize = 16.sp,
            lineHeight = 24.sp,
            color = Color(0xFFCBD5E1)
        )
    }
}

@Composable
private fun PopularReviewsSection(reviews: List<Review>) {
    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Popular Reviews",
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF64748B),
                letterSpacing = 1.sp
            )
            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.ChevronLeft,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }
                IconButton(onClick = { }) {
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = null,
                        tint = Color(0xFF64748B),
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }

        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            reviews.forEach { review ->
                ReviewCard(review = review)
            }
        }
    }
}

@Composable
private fun ReviewCard(review: Review) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = GamerDexColors.SurfaceContainerLow
        ),
        shape = RoundedCornerShape(24.dp),
        border = BorderStroke(1.dp, Color.White.copy(alpha = 0.05f))
    ) {
        Column(
            modifier = Modifier.padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                AsyncImage(
                    model = review.avatarUrl,
                    contentDescription = review.username,
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedCornerShape(50))
                        .border(1.dp, GamerDexColors.Primary.copy(alpha = 0.3f), RoundedCornerShape(50)),
                    contentScale = ContentScale.Crop
                )

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = review.username,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        RatingStars(rating = review.rating)
                    }
                    Text(
                        text = "${review.reviewCount} reviews • ${review.likeCount} likes",
                        fontSize = 12.sp,
                        color = Color(0xFF64748B)
                    )
                }
            }

            // Review Text
            Text(
                text = review.text,
                fontSize = 14.sp,
                lineHeight = 20.sp,
                color = Color(0xFFCBD5E1),
                maxLines = 3
            )

            // Actions
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                ActionButton(icon = Icons.Outlined.Favorite, count = review.likes, label = "Like")
                ActionButton(icon = Icons.Default.ChatBubbleOutline, count = review.comments, label = "Comment")
            }
        }
    }
}

@Composable
private fun RatingStars(rating: Float) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(2.dp)
    ) {
        repeat(5) { index ->
            Icon(
                imageVector = when {
                    index < rating.toInt() -> Icons.Default.Star
                    index == rating.toInt() && rating % 1 != 0f -> Icons.Default.StarHalf
                    else -> Icons.Default.StarOutline
                },
                contentDescription = null,
                modifier = Modifier.size(14.sp.value.dp),
                tint = GamerDexColors.Tertiary
            )
        }
    }
}

@Composable
private fun ActionButton(icon: ImageVector, count: String, label: String) {
    Row(
        horizontalArrangement = Arrangement.spacedBy(4.dp),
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.clip(RoundedCornerShape(8.dp))
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            modifier = Modifier.size(18.sp.value.dp),
            tint = Color(0xFF64748B)
        )
        Text(
            text = count,
            fontSize = 12.sp,
            color = Color(0xFF64748B)
        )
    }
}



// SAMPLE DATA

private val sampleGame = Game(
    id = 1020L,
    title = "Cyberpunk 2077",
    year = 2020,
    developer = "CD Projekt Red",
    backdropUrl = "https://images.unsplash.com/photo-1605899435973-ca2d1a8861cf",
    coverUrl = "https://images.unsplash.com/photo-1605899435973-ca2d1a8861cf",
    rating = 4.2f,
    fans = "12.5k",
    reviews = "8.2k",
    genres = listOf("RPG", "Open World"),
    description = "Cyberpunk 2077 is an open-world, action-adventure RPG set in the dark future of Night City.",
    platforms = listOf(
        Platform(name = "Steam"),
        Platform(name = "PS Store")
    ),
    userRating = 9,
    userRatingDate = "Sep 12, 2023"
)

private val sampleReviews = listOf(
    Review(
        id = "1",
        username = "NeonRider",
        avatarUrl = "",
        rating = 4.5f,
        reviewCount = "2,450",
        likeCount = "45.2k",
        text = "An incredible comeback story. The Phantom Liberty expansion makes this a masterpiece.",
        likes = "1.2k",
        comments = "84"
    )
)

// STATES

@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamerDexColors.Background),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(
            color = GamerDexColors.Primary,
            modifier = Modifier.size(48.dp)
        )
    }
}

@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxSize()
            .background(GamerDexColors.Background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier.padding(24.dp)
        ) {
            Icon(
                imageVector = Icons.Default.ErrorOutline,
                contentDescription = null,
                modifier = Modifier.size(56.dp),
                tint = GamerDexColors.Error
            )
            Text(
                text = "Error",
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = Color.White
            )
            Text(
                text = message,
                fontSize = 14.sp,
                color = GamerDexColors.OnSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
            )
            Button(
                onClick = onRetry,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(44.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = GamerDexColors.Primary,
                    contentColor = GamerDexColors.OnPrimary
                ),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Retry", fontWeight = FontWeight.Bold)
            }
        }
    }
}

// PREVIEW

@Preview(showBackground = true)
@Composable
fun GameDetailScreenPreview() {
    MaterialTheme {
        GameDetailScreenContent(
            uiState = GameDetailUiState.Success(sampleGame),
            userLog = null,
            onRetry = {}
        )
    }
}

