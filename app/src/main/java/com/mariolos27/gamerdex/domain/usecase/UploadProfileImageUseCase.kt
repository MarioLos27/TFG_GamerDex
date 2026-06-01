package com.mariolos27.gamerdex.domain.usecase

import android.net.Uri
import com.mariolos27.gamerdex.domain.repository.UserRepository
import javax.inject.Inject

/**
 * Caso de uso para subir una imagen de perfil desde el almacenamiento local
 * a Firebase Storage y obtener la URL pública.
 */
class UploadProfileImageUseCase @Inject constructor(
    private val userRepository: UserRepository
) {
    suspend operator fun invoke(imageUri: Uri): Result<String> {
        return userRepository.uploadProfileImage(imageUri)
    }
}
