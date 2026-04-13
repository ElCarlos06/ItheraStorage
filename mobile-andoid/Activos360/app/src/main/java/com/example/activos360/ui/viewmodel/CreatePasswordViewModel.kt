package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ChangePasswordDTO
import com.example.activos360.core.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreatePasswordUiState(
    val isLoading: Boolean       = false,
    val errorMessage: String?    = null,
    val successMessage: String?  = null
)

class CreatePasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(CreatePasswordUiState())
    val uiState: StateFlow<CreatePasswordUiState> = _uiState

    fun resetWithToken(token: String, newPassword: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = CreatePasswordUiState(isLoading = true)
            val result = AuthRepository.changePassword(
                ChangePasswordDTO(passwordNueva = newPassword, token = token.trim())
            )
            result.fold(
                onSuccess = {
                    _uiState.value = CreatePasswordUiState(successMessage = it)
                    onSuccess?.invoke()
                },
                onFailure = {
                    _uiState.value = CreatePasswordUiState(
                        errorMessage = it.localizedMessage
                            ?: "No se pudo actualizar la contraseña. Debe tener mayúsculas, minúsculas, número y carácter especial."
                    )
                }
            )
        }
    }

    fun changeOnFirstLogin(
        correo: String,
        passwordActual: String,
        newPassword: String,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            _uiState.value = CreatePasswordUiState(isLoading = true)
            val result = AuthRepository.changePassword(
                ChangePasswordDTO(
                    passwordNueva  = newPassword,
                    correo         = correo.trim(),
                    passwordActual = passwordActual
                )
            )
            result.fold(
                onSuccess = {
                    _uiState.value = CreatePasswordUiState(successMessage = it)
                    onSuccess?.invoke()
                },
                onFailure = {
                    _uiState.value = CreatePasswordUiState(
                        errorMessage = it.localizedMessage ?: "No se pudo actualizar la contraseña (${it.message})"
                    )
                }
            )
        }
    }
}
