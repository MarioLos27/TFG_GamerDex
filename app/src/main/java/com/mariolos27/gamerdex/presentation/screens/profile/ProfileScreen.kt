package com.mariolos27.gamerdex.presentation.screens.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.outlined.*
import androidx.compose.material3.*
import androidx.compose.ui.res.painterResource
import com.mariolos27.gamerdex.R
import com.mariolos27.gamerdex.presentation.components.GamerBottomNavigation
import com.mariolos27.gamerdex.presentation.navigation.Screen
import com.mariolos27.gamerdex.presentation.screens.profile.model.ProfileUiState
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import android.net.Uri
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel
import coil.compose.AsyncImage
import com.mariolos27.gamerdex.domain.model.AppUser

// ─────────────────────────── Color Palette ───────────────────────────
private val DarkBg = Color(0xFF0F1115)
private val CardBg = Color(0xFF1E2128)
private val CardBgAlt = Color(0xFF2A2D35)
private val AccentPurple = Color(0xFF7C3AED)
private val AccentPurpleDark = Color(0xFF9333EA)
private val TextPrimary = Color(0xFFFFFFFF)
private val TextSecondary = Color(0xFF8B949E)
private val TextMuted = Color(0xFF64748B)
private val NavBarBg = Color(0xFF1E2128)
private val SignOutRed = Color(0xFFEF4444)

//  ProfileScreen — Entry Point

@Composable
fun ProfileScreen(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToDashboard: () -> Unit = {},
    onNavigateToLibrary: () -> Unit = {},
    onLogout: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()
    val isEditing by viewModel.isEditing.collectAsState()
    val usernameInput by viewModel.usernameInput.collectAsState()
    val bioInput by viewModel.bioInput.collectAsState()
    val profileImageUrlInput by viewModel.profileImageUrlInput.collectAsState()
    val editError by viewModel.editError.collectAsState()
    val isSaving by viewModel.isSaving.collectAsState()
    val isUploadingImage by viewModel.isUploadingImage.collectAsState()
    val formattedCreatedAt by viewModel.formattedCreatedAt.collectAsState()

    when (val state = uiState) {
        is ProfileUiState.Loading -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = AccentPurple)
            }
        }

        is ProfileUiState.Error -> {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(DarkBg),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = state.message, color = Color.White)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { viewModel.retry() },
                        colors = ButtonDefaults.buttonColors(containerColor = AccentPurple)
                    ) {
                        Text("Retry", color = DarkBg)
                    }
                }
            }
        }

        is ProfileUiState.Success -> {
            ProfileContent(
                user = state.user,
                isEditing = isEditing,
                usernameInput = usernameInput,
                bioInput = bioInput,
                profileImageUrlInput = profileImageUrlInput,
                editError = editError,
                isSaving = isSaving,
                isUploadingImage = isUploadingImage,
                formattedCreatedAt = formattedCreatedAt,
                recentGames = state.recentGames,
                favoriteGames = state.favoriteGames,
                onUsernameChange = viewModel::onUsernameChanged,
                onBioChange = viewModel::onBioChanged,
                onProfileImageUrlChange = viewModel::onProfileImageUrlChanged,
                onUploadProfileImage = viewModel::uploadProfileImage,
                onStartEdit = viewModel::startEditing,
                onCancelEdit = viewModel::cancelEditing,
                onSaveProfile = viewModel::saveProfile,
                onSignOut = {
                    viewModel.signOut()
                    onLogout()
                },
                onNavigateToDashboard = onNavigateToDashboard,
                onNavigateToLibrary = onNavigateToLibrary
            )
        }
    }
}

//  ProfileContent — Main Layout

@Composable
private fun ProfileContent(
    user: AppUser,
    isEditing: Boolean,
    usernameInput: String,
    bioInput: String,
    profileImageUrlInput: String,
    editError: String?,
    isSaving: Boolean,
    isUploadingImage: Boolean,
    formattedCreatedAt: String?,
    recentGames: List<com.mariolos27.gamerdex.domain.model.UserGame>,
    favoriteGames: List<com.mariolos27.gamerdex.domain.model.UserGame>,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onProfileImageUrlChange: (String) -> Unit,
    onUploadProfileImage: (Uri) -> Unit,
    onStartEdit: () -> Unit,
    onCancelEdit: () -> Unit,
    onSaveProfile: () -> Unit,
    onSignOut: () -> Unit,
    onNavigateToDashboard: () -> Unit,
    onNavigateToLibrary: () -> Unit
) {
    var showSignOutDialog by remember { mutableStateOf(false) }

    // Sign Out Confirmation Dialog
    if (showSignOutDialog) {
        AlertDialog(
            onDismissRequest = { showSignOutDialog = false },
            containerColor = CardBg,
            titleContentColor = TextPrimary,
            textContentColor = TextSecondary,
            title = { Text("Sign Out") },
            text = { Text("Are you sure you want to sign out?") },
            confirmButton = {
                Button(
                    onClick = {
                        showSignOutDialog = false
                        onSignOut()
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = SignOutRed)
                ) {
                    Text("Sign Out", color = Color.White)
                }
            },
            dismissButton = {
                TextButton(onClick = { showSignOutDialog = false }) {
                    Text("Cancel", color = TextSecondary)
                }
            }
        )
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(DarkBg)
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = 80.dp)
        ) {
            // Profile Header
            item {
                ProfileHeader(
                    user = user,
                    isEditing = isEditing,
                    usernameInput = usernameInput,
                    bioInput = bioInput,
                    profileImageUrlInput = profileImageUrlInput,
                    editError = editError,
                    isSaving = isSaving,
                    isUploadingImage = isUploadingImage,
                    formattedCreatedAt = formattedCreatedAt,
                    onEditClick = onStartEdit,
                    onUsernameChange = onUsernameChange,
                    onBioChange = onBioChange,
                    onProfileImageUrlChange = onProfileImageUrlChange,
                    onUploadProfileImage = onUploadProfileImage,
                    onCancelEdit = onCancelEdit,
                    onSaveProfile = onSaveProfile,
                    onSignOutClick = { showSignOutDialog = true }
                )
            }

            // Stats Bento
            item { StatsBento(user = user) }

            // Favorite Games Section
            item { FavoriteGamesSection(favoriteGames) }

            // Recent Activity Section
            item { RecentActivitySection(recentGames) }

            // Sign Out Button
            item { SignOutButton(onClick = { showSignOutDialog = true }) }

            // Bottom spacing
            item { Spacer(modifier = Modifier.height(16.dp)) }
        }

        // Bottom Navigation
        GamerBottomNavigation(
            currentRoute = Screen.Profile.route,
            onNavigateToHome = onNavigateToDashboard,
            onNavigateToLibrary = onNavigateToLibrary,
            onNavigateToProfile = { /* Already on profile */ },
            isUserAuthenticated = true, // If we are on Profile Screen, user is authenticated
            modifier = Modifier.align(Alignment.BottomCenter)
        )
    }
}

//  ProfileHeader — Avatar + User Info + Edit Mode

@Composable
private fun ProfileHeader(
    user: AppUser,
    isEditing: Boolean,
    usernameInput: String,
    bioInput: String,
    profileImageUrlInput: String,
    editError: String?,
    isSaving: Boolean,
    isUploadingImage: Boolean,
    formattedCreatedAt: String?,
    onEditClick: () -> Unit,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onProfileImageUrlChange: (String) -> Unit,
    onUploadProfileImage: (Uri) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveProfile: () -> Unit,
    onSignOutClick: () -> Unit
) {
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            onUploadProfileImage(uri)
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        shape = RoundedCornerShape(20.dp),
        colors = CardDefaults.cardColors(containerColor = CardBg.copy(alpha = 0.85f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Sign-out icon in top-right
            Box(modifier = Modifier.fillMaxWidth()) {
                IconButton(
                    onClick = onSignOutClick,
                    modifier = Modifier.align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Outlined.Logout,
                        contentDescription = "Sign Out",
                        tint = TextMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Avatar with gradient border
            Box(
                modifier = Modifier.size(128.dp),
                contentAlignment = Alignment.Center
            ) {
                // Gradient border
                Box(
                    modifier = Modifier
                        .size(128.dp)
                        .border(
                            width = 3.dp,
                            brush = Brush.linearGradient(
                                colors = listOf(AccentPurple, AccentPurpleDark, AccentPurple)
                            ),
                            shape = CircleShape
                        )
                        .padding(4.dp)
                        .clip(CircleShape)
                        .background(CardBgAlt)
                ) {
                    val imageUrl = if (isEditing) {
                        profileImageUrlInput.ifEmpty { user.profileImageUrl }
                    } else {
                        user.profileImageUrl
                    }

                    val imageModel: Any? = if (imageUrl?.startsWith("data:image") == true) {
                        try {
                            val base64String = imageUrl.substringAfter("base64,")
                            val imageBytes = android.util.Base64.decode(base64String, android.util.Base64.DEFAULT)
                            java.nio.ByteBuffer.wrap(imageBytes)
                        } catch (e: Exception) {
                            imageUrl
                        }
                    } else {
                        imageUrl?.ifEmpty { null }
                    }

                    AsyncImage(
                        model = imageModel,
                        contentDescription = "Profile",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = ContentScale.Crop
                    )

                    // Placeholder icon when no image
                    if (imageUrl.isNullOrEmpty()) {
                        Box(
                            modifier = Modifier.fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Person,
                                contentDescription = null,
                                tint = TextMuted,
                                modifier = Modifier.size(48.dp)
                            )
                        }
                    }
                }

                // Camera icon overlay (bottom-right)
                if (isEditing) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomEnd)
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(AccentPurple)
                            .clickable(enabled = !isUploadingImage) { imagePickerLauncher.launch("image/*") },
                        contentAlignment = Alignment.Center
                    ) {
                        if (isUploadingImage) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(18.dp),
                                color = DarkBg,
                                strokeWidth = 2.dp
                            )
                        } else {
                            Icon(
                                imageVector = Icons.Outlined.CameraAlt,
                                contentDescription = "Change photo",
                                tint = DarkBg,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // ── Edit Mode ──
            if (isEditing) {
                EditProfileForm(
                    usernameInput = usernameInput,
                    bioInput = bioInput,
                    profileImageUrlInput = profileImageUrlInput,
                    editError = editError,
                    isSaving = isSaving,
                    onUsernameChange = onUsernameChange,
                    onBioChange = onBioChange,
                    onProfileImageUrlChange = onProfileImageUrlChange,
                    onCancelEdit = onCancelEdit,
                    onSaveProfile = onSaveProfile
                )
            } else {
                // ── View Mode ──
                // Username
                Text(
                    text = user.username?.ifEmpty { "Enter your username" }
                        ?: "Enter your username",
                    fontSize = 26.sp,
                    fontWeight = FontWeight.Bold,
                    color = TextPrimary,
                    textAlign = TextAlign.Center
                )

                // Member since
                if (formattedCreatedAt != null) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Member since $formattedCreatedAt",
                        fontSize = 12.sp,
                        color = TextSecondary
                    )
                }

                // Bio
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = user.bio?.ifEmpty { "No biography" } ?: "No biography",
                    fontSize = 14.sp,
                    color = if (user.bio.isNullOrEmpty()) TextMuted else TextSecondary,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Edit Profile Button
                Button(
                    onClick = onEditClick,
                    modifier = Modifier.height(40.dp),
                    shape = RoundedCornerShape(20.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = CardBgAlt)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Edit,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = AccentPurple
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Edit Profile", fontSize = 13.sp, color = TextPrimary)
                }
            }
        }
    }
}

//  EditProfileForm — Username + Bio + Image URL fields

@Composable
private fun EditProfileForm(
    usernameInput: String,
    bioInput: String,
    profileImageUrlInput: String,
    editError: String?,
    isSaving: Boolean,
    onUsernameChange: (String) -> Unit,
    onBioChange: (String) -> Unit,
    onProfileImageUrlChange: (String) -> Unit,
    onCancelEdit: () -> Unit,
    onSaveProfile: () -> Unit
) {
    val textFieldColors = OutlinedTextFieldDefaults.colors(
        focusedTextColor = Color.White,
        unfocusedTextColor = Color.White,
        focusedBorderColor = AccentPurple,
        unfocusedBorderColor = TextSecondary,
        focusedLabelColor = AccentPurple,
        unfocusedLabelColor = TextSecondary,
        cursorColor = AccentPurple
    )

    Column(
        modifier = Modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        // Username field
        OutlinedTextField(
            value = usernameInput,
            onValueChange = onUsernameChange,
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isSaving,
            leadingIcon = {
                Icon(Icons.Outlined.AlternateEmail, null, tint = TextMuted)
            },
            supportingText = {
                Text(
                    "${usernameInput.length}/15",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        )

        // Bio field
        OutlinedTextField(
            value = bioInput,
            onValueChange = onBioChange,
            label = { Text("Biography") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            maxLines = 3,
            enabled = !isSaving,
            leadingIcon = {
                Icon(Icons.Outlined.Info, null, tint = TextMuted)
            },
            supportingText = {
                Text(
                    "${bioInput.length}/150",
                    color = TextMuted,
                    fontSize = 11.sp
                )
            }
        )

        // Profile Image URL field
        OutlinedTextField(
            value = profileImageUrlInput,
            onValueChange = onProfileImageUrlChange,
            label = { Text("Profile image URL") },
            modifier = Modifier.fillMaxWidth(),
            colors = textFieldColors,
            singleLine = true,
            enabled = !isSaving,
            leadingIcon = {
                Icon(Icons.Outlined.Link, null, tint = TextMuted)
            }
        )

        // Error message
        if (editError != null) {
            Text(
                text = editError,
                color = SignOutRed,
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        // Action buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)
        ) {
            TextButton(onClick = onCancelEdit, enabled = !isSaving) {
                Text("Cancel", color = TextSecondary)
            }
            Button(
                onClick = onSaveProfile,
                enabled = !isSaving,
                colors = ButtonDefaults.buttonColors(containerColor = AccentPurple),
                shape = RoundedCornerShape(20.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = DarkBg
                    )
                } else {
                    Text("Save", color = DarkBg, fontWeight = FontWeight.SemiBold)
                }
            }
        }
    }
}

//  StatsBento — Dynamic stat cards from AppUser

@Composable
private fun StatsBento(user: AppUser) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = formatStatNumber(user.gamesCount),
                label = "Games",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = formatStatNumber(user.reviewsCount),
                label = "Reviews",
                modifier = Modifier.weight(1f)
            )
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(90.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            StatCard(
                value = formatStatNumber(user.listsCount),
                label = "Lists",
                modifier = Modifier.weight(1f)
            )
            StatCard(
                value = formatStatNumber(user.followersCount),
                label = "Followers",
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@Composable
private fun StatCard(value: String, label: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier.fillMaxSize(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgAlt.copy(alpha = 0.8f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = value,
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = AccentPurple
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = label,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextSecondary,
                textAlign = TextAlign.Center
            )
        }
    }
}

/**
 * Formats a stat number: numbers >= 1000 become "1.2k", etc.
 */
private fun formatStatNumber(count: Int): String {
    return when {
        count >= 1_000_000 -> String.format("%.1fM", count / 1_000_000.0)
        count >= 1_000 -> String.format("%.1fk", count / 1_000.0)
        else -> count.toString()
    }
}

//  FavoriteGamesSection — Empty State

@Composable
private fun FavoriteGamesSection(games: List<com.mariolos27.gamerdex.domain.model.UserGame>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Icon(
                    imageVector = Icons.Outlined.Favorite,
                    contentDescription = null,
                    tint = AccentPurple,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = "Favorites",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = TextPrimary
                )
            }
        }

        if (games.isEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                EmptyStateCard(
                    icon = Icons.Outlined.FavoriteBorder,
                    message = "No favorite games yet"
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games.size) { index ->
                    GameCoverItem(games[index])
                }
            }
        }
    }
}

//  RecentActivitySection — Empty State

@Composable
private fun RecentActivitySection(games: List<com.mariolos27.gamerdex.domain.model.UserGame>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 16.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .padding(bottom = 12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Recent Activity",
                fontSize = 18.sp,
                fontWeight = FontWeight.SemiBold,
                color = TextPrimary
            )
        }

        if (games.isEmpty()) {
            Box(modifier = Modifier.padding(horizontal = 16.dp)) {
                EmptyStateCard(
                    icon = Icons.Outlined.History,
                    message = "No recent activity"
                )
            }
        } else {
            androidx.compose.foundation.lazy.LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(games.size) { index ->
                    GameCoverItem(games[index])
                }
            }
        }
    }
}

@Composable
private fun GameCoverItem(userGame: com.mariolos27.gamerdex.domain.model.UserGame) {
    Card(
        modifier = Modifier
            .width(100.dp)
            .aspectRatio(2f / 3f),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgAlt)
    ) {
        AsyncImage(
            model = userGame.gameCoverUrl,
            contentDescription = userGame.gameTitle,
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )
    }
}

//  EmptyStateCard — Reusable empty state composable

@Composable
private fun EmptyStateCard(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    message: String
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = CardBgAlt.copy(alpha = 0.4f))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = TextMuted,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(12.dp))
            Text(
                text = message,
                fontSize = 14.sp,
                color = TextMuted,
                textAlign = TextAlign.Center
            )
        }
    }
}

//  SignOutButton — Bottom of the list

@Composable
private fun SignOutButton(onClick: () -> Unit) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        contentAlignment = Alignment.Center
    ) {
        OutlinedButton(
            onClick = onClick,
            shape = RoundedCornerShape(12.dp),
            border = BorderStroke(1.dp, SignOutRed.copy(alpha = 0.5f)),
            colors = ButtonDefaults.outlinedButtonColors(contentColor = SignOutRed)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Outlined.Logout,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text("Sign Out", fontSize = 14.sp)
        }
    }

}
