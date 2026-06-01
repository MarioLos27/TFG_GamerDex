package com.mariolos27.gamerdex.presentation.screens.library.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.mariolos27.gamerdex.presentation.screens.library.extensions.borderTop
import com.mariolos27.gamerdex.presentation.screens.library.model.GameLibraryItem
import com.mariolos27.gamerdex.domain.model.GameStatus

@Composable
fun GameLibraryCard(
    game: GameLibraryItem,
    onGameClick: (Long) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable { onGameClick(game.id) }
            .background(Color(0xFF1E2128).copy(alpha = 0.8f)),
        color = Color.Transparent
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            // Image Section
            GameCardImageSection(game = game)

            // Info Section
            GameCardInfoSection(
                game = game,
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun GameCardImageSection(
    game: GameLibraryItem,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(160.dp)
            .background(Color(0xFF0F1115))
    ) {
        // Game Image
        AsyncImage(
            model = game.imageUrl,
            contentDescription = game.title,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        // Gradient Overlay
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    brush = Brush.verticalGradient(
                        colors = listOf(
                            Color.Transparent,
                            Color(0xFF0B1326).copy(alpha = 0.6f)
                        )
                    )
                )
        )

        // Status Badge
        if (game.status == GameStatus.PLAYING) {
            Surface(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .padding(8.dp),
                color = Color(0xFFB76DFF),
                shape = RoundedCornerShape(4.dp)
            ) {
                Text(
                    "Playing",
                    fontSize = 10.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF400071),
                    modifier = Modifier.padding(4.dp)
                )
            }
        }
    }
}

@Composable
private fun ColumnScope.GameCardInfoSection(
    game: GameLibraryItem,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(Color(0xFF1E2128).copy(alpha = 0.9f))
            .padding(12.dp),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Title & Platform
            GameCardHeader(game = game)

            // Progress Section
            if (game.progress > 0) {
                GameCardProgress(game = game)
            }
        }

        // Bottom Stats
        GameCardFooter(game = game)
    }
}

@Composable
private fun GameCardHeader(
    game: GameLibraryItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = game.title,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = Color.White,
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )
        Surface(
            modifier = Modifier
                .background(Color(0xFF2A2D35), RoundedCornerShape(2.dp))
                .padding(2.dp),
            color = Color.Transparent
        ) {
            Text(
                text = game.platform,
                fontSize = 9.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFF8B949E),
                modifier = Modifier.padding(2.dp)
            )
        }
    }
}

@Composable
private fun GameCardProgress(
    game: GameLibraryItem,
    modifier: Modifier = Modifier
) {
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(4.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                "Progress",
                fontSize = 10.sp,
                color = Color(0xFF8B949E)
            )
            Text(
                "${game.progress}%",
                fontSize = 10.sp,
                color = Color(0xFFDDB7FF),
                fontWeight = FontWeight.Bold
            )
        }
        LinearProgressIndicator(
            progress = { game.progress.toFloat() / 100f },
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .clip(RoundedCornerShape(1.dp)),
            color = Color(0xFFB76DFF),
            trackColor = Color(0xFF0B1326)
        )
    }
}

@Composable
private fun GameCardFooter(
    game: GameLibraryItem,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(top = 8.dp)
            .borderTop(
                width = 1.dp,
                color = Color(0xFF2A2D35)
            )
            .padding(top = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("⏱️", fontSize = 12.sp)
            Text(
                when {
                    game.totalHours > 0 -> "${game.totalHours.toInt()} hrs"
                    game.status == GameStatus.WISHLIST -> "Wishlist"
                    else -> "0 hrs"
                },
                fontSize = 10.sp,
                color = Color(0xFF8B949E)
            )
        }

        IconButton(
            onClick = { /* TODO: Show options menu */ },
            modifier = Modifier.size(24.dp)
        ) {
            Text("⋮", fontSize = 14.sp, color = Color(0xFF8B949E))
        }
    }
}

