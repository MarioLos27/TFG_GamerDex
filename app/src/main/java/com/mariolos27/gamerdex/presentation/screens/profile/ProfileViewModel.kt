package com.mariolos27.gamerdex.presentation.screens.profile

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.model.AppUser
import com.mariolos27.gamerdex.domain.repository.UserRepository
import android.net.Uri
import com.mariolos27.gamerdex.domain.usecase.GetAllUserGamesUseCase
import com.mariolos27.gamerdex.domain.usecase.GetCurrentUserUseCase
import com.mariolos27.gamerdex.domain.usecase.LogoutUseCase
import com.mariolos27.gamerdex.domain.usecase.UploadProfileImageUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import com.mariolos27.gamerdex.presentation.screens.profile.model.ProfileUiState

@HiltViewModel
class ProfileViewModel @Inject constructor(
    private val getCurrentUserUseCase: GetCurrentUserUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val userRepository: UserRepository,
    private val getAllUserGamesUseCase: GetAllUserGamesUseCase,
    private val uploadProfileImageUseCase: UploadProfileImageUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<ProfileUiState>(ProfileUiState.Loading)
    val uiState: StateFlow<ProfileUiState> = _uiState.asStateFlow()

    // Editing state
    private val _isEditing = MutableStateFlow(false)
    val isEditing: StateFlow<Boolean> = _isEditing.asStateFlow()

    private val _usernameInput = MutableStateFlow("")
    val usernameInput: StateFlow<String> = _usernameInput.asStateFlow()

    private val _bioInput = MutableStateFlow("")
    val bioInput: StateFlow<String> = _bioInput.asStateFlow()

    private val _profileImageUrlInput = MutableStateFlow("")
    val profileImageUrlInput: StateFlow<String> = _profileImageUrlInput.asStateFlow()

    private val _editError = MutableStateFlow<String?>(null)
    val editError: StateFlow<String?> = _editError.asStateFlow()

    private val _isSaving = MutableStateFlow(false)
    val isSaving: StateFlow<Boolean> = _isSaving.asStateFlow()

    private val _isUploadingImage = MutableStateFlow(false)
    val isUploadingImage: StateFlow<Boolean> = _isUploadingImage.asStateFlow()

    // Formatted member-since date
    private val _formattedCreatedAt = MutableStateFlow<String?>(null)
    val formattedCreatedAt: StateFlow<String?> = _formattedCreatedAt.asStateFlow()

    init {
        loadUserProfile()
    }

    private var gamesJob: Job? = null

    private fun loadUserProfile() {
        viewModelScope.launch {
            getCurrentUserUseCase().collect { authUser ->
                if (authUser != null) {
                    // Datos completos del perfil personal cargados desde la firestore
                    val usernameResult = userRepository.getCurrentUsername()
                    val username = usernameResult.getOrNull() ?: authUser.username

                    gamesJob?.cancel()
                    gamesJob = launch {
                        getAllUserGamesUseCase().collect { userGames ->
                            val gamesCount = userGames.size
                            val reviewsCount = userGames.count { !it.review.isNullOrBlank() }
                            
                            // Top 5 favorite games
                            val favoriteGames = userGames
                                .filter { it.isFavorite }
                                .sortedByDescending { it.lastUpdated }
                                .take(5)

                            // Top 5 recent activities
                            val recentGames = userGames
                                .sortedByDescending { it.lastUpdated }
                                .take(5)
                            
                            val user = authUser.copy(
                                username = username,
                                gamesCount = gamesCount,
                                reviewsCount = reviewsCount
                            )
                            _uiState.value = ProfileUiState.Success(
                                user = user,
                                recentGames = recentGames,
                                favoriteGames = favoriteGames
                            )

                            // Initialize edit fields
                            _usernameInput.value = username ?: ""
                            _bioInput.value = user.bio ?: ""
                            _profileImageUrlInput.value = user.profileImageUrl ?: ""

                            // Format the createdAt date
                            _formattedCreatedAt.value = formatCreatedAt(user.createdAt)
                        }
                    }
                } else {
                    gamesJob?.cancel()
                    _uiState.value = ProfileUiState.Error("Could not load profile")
                }
            }
        }
    }

    fun onUsernameChanged(newUsername: String) {
        if (newUsername.length <= 15) {
            _usernameInput.value = newUsername
            _editError.value = null
        }
    }

    fun onBioChanged(newBio: String) {
        if (newBio.length <= 150) {
            _bioInput.value = newBio
            _editError.value = null
        }
    }

    fun onProfileImageUrlChanged(newUrl: String) {
        _profileImageUrlInput.value = newUrl
        _editError.value = null
    }

    fun startEditing() {
        _isEditing.value = true
    }

    fun cancelEditing() {
        _isEditing.value = false
        val currentState = _uiState.value
        if (currentState is ProfileUiState.Success) {
            _usernameInput.value = currentState.user.username ?: ""
            _bioInput.value = currentState.user.bio ?: ""
            _profileImageUrlInput.value = currentState.user.profileImageUrl ?: ""
        }
        _editError.value = null
    }

    fun saveProfile() {
        val newUsername = _usernameInput.value.trim()
        val newBio = _bioInput.value.trim()
        val newImageUrl = _profileImageUrlInput.value.trim()

        if (newUsername.isEmpty()) {
            _editError.value = "Username cannot be empty"
            return
        }

        if (newUsername.length < 3) {
            _editError.value = "Username must be at least 3 characters"
            return
        }

        viewModelScope.launch {
            _isSaving.value = true
            val result = userRepository.updateUserProfile(
                username = newUsername,
                bio = newBio,
                profileImageUrl = newImageUrl
            )
            _isSaving.value = false

            result.onSuccess {
                _isEditing.value = false
                loadUserProfile() // Recargar para obtener los datos actualizados
            }.onFailure { exception ->
                _editError.value = exception.message ?: "Error saving profile"
            }
        }
    }

    fun uploadProfileImage(uri: Uri) {
        viewModelScope.launch {
            _isUploadingImage.value = true
            _editError.value = null
            
            val result = uploadProfileImageUseCase(uri)
            
            _isUploadingImage.value = false
            
            result.onSuccess { downloadUrl ->
                _profileImageUrlInput.value = downloadUrl
                _isEditing.value = true
            }.onFailure { exception ->
                _editError.value = exception.message ?: "Error uploading image"
                _isEditing.value = true
            }
        }
    }

    fun signOut() {
        viewModelScope.launch {
            logoutUseCase()
        }
    }

    fun retry() {
        _uiState.value = ProfileUiState.Loading
        loadUserProfile()
    }

    /**
     * Formatea un timestamp en millis (almacenado como String) a "MMM yyyy".
     * Retorna null si el valor es nulo o inválido.
     */
    private fun formatCreatedAt(timestamp: String?): String? {
        if (timestamp.isNullOrBlank()) return null
        return try {
            val millis = timestamp.toLong()
            val date = Date(millis)
            val formatter = SimpleDateFormat("MMM yyyy", Locale.getDefault())
            formatter.format(date)
        } catch (e: NumberFormatException) {
            null
        }
    }
}


