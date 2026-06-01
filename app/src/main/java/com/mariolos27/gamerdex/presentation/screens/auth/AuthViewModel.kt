package com.mariolos27.gamerdex.presentation.screens.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.mariolos27.gamerdex.domain.usecase.GetCurrentUserUseCase
import com.mariolos27.gamerdex.domain.usecase.LoginUseCase
import com.mariolos27.gamerdex.domain.usecase.LogoutUseCase
import com.mariolos27.gamerdex.domain.usecase.RegisterUseCase
import com.mariolos27.gamerdex.presentation.screens.auth.model.AuthFormState
import com.mariolos27.gamerdex.presentation.screens.auth.model.AuthUiState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val getCurrentUserUseCase: GetCurrentUserUseCase
) : ViewModel() {

    private val _authUiState = MutableStateFlow<AuthUiState>(AuthUiState.Idle)
    val authUiState: StateFlow<AuthUiState> = _authUiState.asStateFlow()

    private val _formState = MutableStateFlow(AuthFormState())
    val formState: StateFlow<AuthFormState> = _formState.asStateFlow()

    init {
        observeCurrentUser()
    }

    fun updateEmail(email: String) {
        _formState.value = _formState.value.copy(
            email = email,
            isEmailValid = isValidEmail(email)
        )
    }

    fun updatePassword(password: String) {
        _formState.value = _formState.value.copy(
            password = password,
            isPasswordValid = password.isNotEmpty()
        )
    }

    fun login() {
        val form = _formState.value
        if (form.email.isBlank() || form.password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Please fill all fields")
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val result = loginUseCase(form.email, form.password)
            result.onSuccess { user ->
                _authUiState.value = AuthUiState.Success(user)
                clearForm()
            }.onFailure { error ->
                _authUiState.value = AuthUiState.Error(
                    error.message ?: "Unknown login error"
                )
            }
        }
    }

    fun register() {
        val form = _formState.value
        if (form.email.isBlank() || form.password.isBlank()) {
            _authUiState.value = AuthUiState.Error("Please fill all fields")
            return
        }
        if (!isValidEmail(form.email)) {
            _authUiState.value = AuthUiState.Error("Invalid email")
            return
        }
        if (form.password.length < 6) {
            _authUiState.value = AuthUiState.Error("Password must be at least 6 characters")
            return
        }

        viewModelScope.launch {
            _authUiState.value = AuthUiState.Loading
            val result = registerUseCase(form.email, form.password)
            result.onSuccess { user ->
                _authUiState.value = AuthUiState.Success(user)
                clearForm()
            }.onFailure { error ->
                _authUiState.value = AuthUiState.Error(
                    error.message ?: "Unknown registration error"
                )
            }
        }
    }

    fun logout() {
        viewModelScope.launch {
            val result = logoutUseCase()
            result.onSuccess {
                _authUiState.value = AuthUiState.Unauthenticated
                clearForm()
            }.onFailure { error ->
                _authUiState.value = AuthUiState.Error(
                    error.message ?: "Error signing out"
                )
            }
        }
    }

    private fun observeCurrentUser() {
        getCurrentUserUseCase()
            .onEach { user ->
                if (user != null) {
                    _authUiState.value = AuthUiState.Success(user)
                } else {
                    if (_authUiState.value !is AuthUiState.Loading) {
                        _authUiState.value = AuthUiState.Unauthenticated
                    }
                }
            }
            .launchIn(viewModelScope)
    }

    private fun clearForm() {
        _formState.value = AuthFormState()
    }

    private fun isValidEmail(email: String): Boolean {
        return email.contains("@") && email.contains(".")
    }

    fun clearError() {
        _authUiState.value = AuthUiState.Idle
    }
}
