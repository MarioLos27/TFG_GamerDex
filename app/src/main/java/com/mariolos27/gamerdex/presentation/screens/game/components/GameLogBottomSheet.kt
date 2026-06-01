package com.mariolos27.gamerdex.presentation.screens.game.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import com.mariolos27.gamerdex.presentation.screens.game.model.GameLogUiState
import com.mariolos27.gamerdex.presentation.screens.game.GameLogViewModel
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.StarOutline
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import com.mariolos27.gamerdex.domain.model.Game
import com.mariolos27.gamerdex.domain.model.GameStatus

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun GameLogBottomSheet(
    game: Game,
    onDismiss: () -> Unit,
    viewModel: GameLogViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val status by viewModel.status.collectAsState()
    val rating by viewModel.rating.collectAsState()
    val review by viewModel.review.collectAsState()
    val hoursPlayed by viewModel.hoursPlayed.collectAsState()
    val platform by viewModel.platform.collectAsState()
    val isFavorite by viewModel.isFavorite.collectAsState()

    LaunchedEffect(game.id) {
        viewModel.loadUserGame(game.id)
    }

    LaunchedEffect(uiState) {
        if (uiState is GameLogUiState.Saved || uiState is GameLogUiState.Deleted) {
            onDismiss()
        }
    }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = Color(0xFF171F33),
        contentColor = Color.White,
        dragHandle = { BottomSheetDefaults.DragHandle(color = Color(0xFF94A3B8)) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp)
                .padding(bottom = 32.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Log ${game.title}",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Row {
                    val isExistingLog = (uiState as? GameLogUiState.Success)?.userGame != null
                    
                    IconButton(onClick = { viewModel.toggleFavorite() }) {
                        Icon(
                            imageVector = if (isFavorite) Icons.Filled.Favorite else Icons.Outlined.FavoriteBorder,
                            contentDescription = "Favorite",
                            tint = if (isFavorite) Color(0xFFF43F5E) else Color(0xFF94A3B8)
                        )
                    }

                    if (isExistingLog) {
                        IconButton(onClick = { viewModel.deleteLog(game.id) }) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = Color(0xFFFFB4AB)
                            )
                        }
                    }
                    IconButton(onClick = onDismiss) {
                        Icon(androidx.compose.material.icons.Icons.Default.Close, contentDescription = "Close")
                    }
                }
            }

            // Status Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "STATUS",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    maxItemsInEachRow = 3
                ) {
                    GameStatus.entries.filter { it != GameStatus.WISHLIST }.forEach { gameStatus ->
                        FilterChip(
                            selected = status == gameStatus,
                            onClick = { viewModel.updateStatus(gameStatus) },
                            label = { Text(gameStatus.name) },
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = Color(0xFFB76DFF),
                                selectedLabelColor = Color(0xFF400071),
                                containerColor = Color(0xFF222A3D),
                                labelColor = Color.White
                            ),
                            border = null
                        )
                    }
                }
            }

            // Rating Selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "RATING",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF94A3B8),
                    letterSpacing = 1.sp
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    (1..10).forEach { i ->
                        Icon(
                            imageVector = if (rating != null && i <= rating!!) Icons.Filled.Star else Icons.Outlined.StarOutline,
                            contentDescription = null,
                            tint = if (rating != null && i <= rating!!) Color(0xFF4EDEA3) else Color(0xFF475569),
                            modifier = Modifier
                                .size(28.dp)
                                .clickable {
                                    viewModel.updateRating(if (rating == i) null else i)
                                }
                        )
                    }
                }
            }

            // Review text
            OutlinedTextField(
                value = review,
                onValueChange = { viewModel.updateReview(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("What did you think?") },
                placeholder = { Text("Add a review...") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB76DFF),
                    unfocusedBorderColor = Color(0xFF475569),
                    focusedLabelColor = Color(0xFFB76DFF),
                    cursorColor = Color(0xFFB76DFF)
                ),
                minLines = 3
            )

            // Platform (Optional)
            OutlinedTextField(
                value = platform ?: "",
                onValueChange = { viewModel.updatePlatform(it) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Platform (Optional)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB76DFF),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )

            // Hours Played (Optional)
            OutlinedTextField(
                value = hoursPlayed?.toString() ?: "",
                onValueChange = { viewModel.updateHours(it.toIntOrNull()) },
                modifier = Modifier.fillMaxWidth(),
                label = { Text("Hours Played (Optional)") },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFFB76DFF),
                    unfocusedBorderColor = Color(0xFF475569)
                )
            )

            // Save Button
            Button(
                onClick = { viewModel.saveLog(game.id, game.title, game.coverUrl) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(56.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFFDDB7FF),
                    contentColor = Color(0xFF490080)
                ),
                shape = RoundedCornerShape(12.dp),
                enabled = uiState !is GameLogUiState.Loading
            ) {
                if (uiState is GameLogUiState.Loading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = Color(0xFF490080))
                } else {
                    Text("Save Log", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }
}
