package com.mariolos27.gamerdex.presentation.screens.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.hilt.navigation.compose.hiltViewModel

import com.mariolos27.gamerdex.presentation.screens.profile.model.UsernameUiState
import com.mariolos27.gamerdex.presentation.screens.profile.UsernameViewModel

/**
 * Pantalla de configuración de username posterior al registro.
 *
 * Permite que el usuario ingrese un nombre de usuario único.
 * Características:
 * - Validación en tiempo real
 * - Indicador de carga mientras se guarda
 * - Manejo de errores con opción de reintentar
 * - Navegación automática al éxito
 *
 * UI:
 * - Fondo oscuro (#0B1326)
 * - TextField con bordes morados
 * - Botón principal en morado (#DDB7FF)
 * - Acento cyan (#4EDEA3)
 */
@Composable
fun UsernameScreen(
    viewModel: UsernameViewModel = hiltViewModel(),
    onUsernameCreated: (username: String) -> Unit
) {
    val uiState = viewModel.uiState.collectAsState().value
    val usernameInput = viewModel.usernameInput.collectAsState().value
    val validationError = viewModel.validationError.collectAsState().value

    // Navegar al éxito cuando se guarde exitosamente
    LaunchedEffect(uiState) {
        if (uiState is UsernameUiState.Success) {
            onUsernameCreated(uiState.username)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                color = Color(0xFF0B1326) // Surface dim
            ),
        contentAlignment = Alignment.Center
    ) {
        when (uiState) {
            is UsernameUiState.Loading -> {
                LoadingState()
            }
            is UsernameUiState.Error -> {
                ErrorState(
                    message = uiState.message,
                    onRetry = {
                        viewModel.resetState()
                    }
                )
            }
            else -> {
                UsernameFormContent(
                    usernameInput = usernameInput,
                    validationError = validationError,
                    onUsernameChanged = viewModel::onUsernameChanged,
                    onSaveClick = viewModel::saveUsername,
                    isLoading = uiState is UsernameUiState.Loading
                )
            }
        }
    }
}

/**
 * Contenido principal: formulario de ingreso de username.
 */
@Composable
private fun UsernameFormContent(
    usernameInput: String,
    validationError: String?,
    onUsernameChanged: (String) -> Unit,
    onSaveClick: () -> Unit,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(24.dp)
    ) {
        // Encabezado
        Text(
            text = "Choose your Username",
            fontSize = 32.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFDDB7FF), // Primary
            textAlign = TextAlign.Center
        )

        Text(
            text = "This will be your unique identifier on GamerDex.\nYou can change it later in your profile.",
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFCAC4D0), // On surface variant
            textAlign = TextAlign.Center
        )

        // TextField de username
        UsernameTextField(
            value = usernameInput,
            onValueChange = onUsernameChanged,
            isError = validationError != null,
            modifier = Modifier.fillMaxWidth()
        )

        // Mensaje de validación/error
        if (validationError != null) {
            Text(
                text = validationError,
                fontSize = 12.sp,
                fontWeight = FontWeight.Medium,
                color = Color(0xFFFFB4AB), // Error
                textAlign = TextAlign.Center
            )
        }

        // Contador de caracteres
        Text(
            text = "${usernameInput.length}/15 characters",
            fontSize = 12.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF988D9F), // Outline
            textAlign = TextAlign.Center
        )

        // Botón de guardar
        Button(
            onClick = onSaveClick,
            enabled = !isLoading && usernameInput.length in 3..15,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDDB7FF), // Primary
                disabledContainerColor = Color(0xFF6900B3).copy(alpha = 0.5f)
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Confirm Username",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0B1326), // On primary
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }

        // Texto informativo
        Text(
            text = "3-15 characters. Only letters, numbers, and underscores (_)",
            fontSize = 11.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFF988D9F), // Outline
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Campo de texto para ingreso de username con validación visual.
 */
@Composable
private fun UsernameTextField(
    value: String,
    onValueChange: (String) -> Unit,
    isError: Boolean,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        modifier = modifier
            .fillMaxWidth(),
        label = {
            Text("Username")
        },
        placeholder = {
            Text("example_user")
        },
        singleLine = true,
        isError = isError,
        shape = RoundedCornerShape(12.dp),
        colors = OutlinedTextFieldDefaults.colors(
            focusedBorderColor = Color(0xFFDDB7FF), // Primary
            unfocusedBorderColor = Color(0xFF2D3449).copy(alpha = 0.5f), // Surface variant
            errorBorderColor = Color(0xFFFFB4AB), // Error
            focusedLabelColor = Color(0xFFDDB7FF),
            unfocusedLabelColor = Color(0xFFCAC4D0),
            focusedTextColor = Color(0xFFDAE2FD), // On background
            unfocusedTextColor = Color(0xFFDAE2FD),
            cursorColor = Color(0xFF4EDEA3), // Tertiary
            errorCursorColor = Color(0xFFFFB4AB)
        ),
        textStyle = androidx.compose.material3.LocalTextStyle.current.copy(
            fontSize = 16.sp
        )
    )
}

/**
 * Estado de carga con indicador de progreso.
 */
@Composable
private fun LoadingState(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp, Alignment.CenterVertically)
    ) {
        CircularProgressIndicator(
            color = Color(0xFFDDB7FF), // Primary
            modifier = Modifier
                .padding(bottom = 16.dp)
        )

        Text(
            text = "Saving your username...",
            fontSize = 16.sp,
            fontWeight = FontWeight.SemiBold,
            color = Color(0xFFDAE2FD), // On background
            textAlign = TextAlign.Center
        )
    }
}

/**
 * Estado de error con mensaje y botón de reintentar.
 */
@Composable
private fun ErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth(0.85f)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "❌ Error",
            fontSize = 20.sp,
            fontWeight = FontWeight.Bold,
            color = Color(0xFFFFB4AB), // Error
            textAlign = TextAlign.Center
        )

        Text(
            text = message,
            fontSize = 14.sp,
            fontWeight = FontWeight.Normal,
            color = Color(0xFFCAC4D0), // On surface variant
            textAlign = TextAlign.Center
        )

        Button(
            onClick = onRetry,
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFFDDB7FF) // Primary
            ),
            shape = RoundedCornerShape(12.dp)
        ) {
            Text(
                text = "Try Again",
                fontSize = 14.sp,
                fontWeight = FontWeight.SemiBold,
                color = Color(0xFF0B1326), // On primary
                modifier = Modifier.padding(vertical = 8.dp)
            )
        }
    }
}


