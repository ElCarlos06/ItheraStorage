package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ChangePasswordDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.AuthRepository
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {

    var isLoading    by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess    by mutableStateOf(false)
        private set

    fun changePassword(assignedPassword: String, newPassword: String) {
        viewModelScope.launch {
            isLoading    = true
            errorMessage = null
            isSuccess    = false
            val correo   = TokenManager.getCorreoFromToken() ?: ""
            val result   = AuthRepository.changePassword(
                ChangePasswordDTO(
                    passwordNueva   = newPassword,
                    correo          = correo,
                    passwordActual  = assignedPassword
                )
            )
            result.fold(
                onSuccess = { isSuccess = true },
                onFailure = { errorMessage = it.localizedMessage ?: "Error al actualizar la contraseña" }
            )
            isLoading = false
        }
    }
}
