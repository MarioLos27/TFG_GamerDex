package com.mariolos27.gamerdex.presentation.screens.profile

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.usecase.SaveUsernameUseCase
import com.mariolos27.gamerdex.presentation.screens.profile.model.UsernameUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

private const val TAG = "UsernameViewModel"

@HiltViewModel
class UsernameViewModel @Inject constructor(
    private val saveUsernameUseCase: SaveUsernameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow<UsernameUiState>(UsernameUiState.Idle)
    val uiState: StateFlow<UsernameUiState> = _uiState.asStateFlow()

    private val _usernameInput = MutableStateFlow("")
    val usernameInput: StateFlow<String> = _usernameInput.asStateFlow()

    private val _validationError = MutableStateFlow<String?>(null)
    val validationError: StateFlow<String?> = _validationError.asStateFlow()

    fun onUsernameChanged(newUsername: String) {
        val limitedUsername = if (newUsername.length > 15) {
            newUsername.substring(0, 15)
        } else {
            newUsername
        }

        _usernameInput.value = limitedUsername
        _validationError.value = null

        when {
            limitedUsername.isEmpty() -> {
                _validationError.value = null
            }
            limitedUsername.length < 3 -> {
                _validationError.value = "Minimum 3 characters"
            }
            !limitedUsername.matches("^[a-zA-Z0-9_]*$".toRegex()) -> {
                _validationError.value = "Only letters, numbers, and underscores"
            }
        }
    }

    fun saveUsername() {
        val username = _usernameInput.value.trim()

        if (username.isEmpty()) {
            _validationError.value = "Enter a username"
            return
        }
        if (username.length < 3) {
            _validationError.value = "Minimum 3 characters"
            return
        }
        if (username.length > 15) {
            _validationError.value = "Maximum 15 characters"
            return
        }

        _uiState.value = UsernameUiState.Loading

        viewModelScope.launch {
            try {
                Log.d(TAG, "Guardando username: $username")
                val result = saveUsernameUseCase(username)

                result.onSuccess {
                    Log.d(TAG, "Username guardado exitosamente: $username")
                    _uiState.value = UsernameUiState.Success(username)
                    _validationError.value = null
                }.onFailure { exception ->
                    Log.e(TAG, "Error al guardar username", exception)
                    _uiState.value = UsernameUiState.Error(
                        message = exception.message ?: "Unknown error",
                        errorCode = exception.javaClass.simpleName
                    )
                }
            } catch (e: Exception) {
                Log.e(TAG, "Excepción no capturada", e)
                _uiState.value = UsernameUiState.Error(
                    message = e.message ?: "Unexpected error",
                    errorCode = "UNEXPECTED_ERROR"
                )
            }
        }
    }

    fun resetState() {
        _uiState.value = UsernameUiState.Idle
        _usernameInput.value = ""
        _validationError.value = null
    }
}
