package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ChangePasswordDTO
import com.example.activos360.core.network.ApiProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class CreatePasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class CreatePasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(CreatePasswordUiState())
    val uiState: StateFlow<CreatePasswordUiState> = _uiState

    fun resetWithToken(token: String, newPassword: String, onSuccess: (() -> Unit)? = null) {
        viewModelScope.launch {
            _uiState.value = CreatePasswordUiState(isLoading = true)
            try {
                val resp = ApiProvider.authApi.changePassword(
                    ChangePasswordDTO(
                        passwordNueva = newPassword,
                        token = token.trim()
                    )
                )
                val body = resp.body()
                if (resp.isSuccessful && body?.error == false) {
                    _uiState.value = CreatePasswordUiState(
                        isLoading = false,
                        successMessage = body.message ?: "Contraseña actualizada"
                    )
                    onSuccess?.invoke()
                } else {
                    _uiState.value = CreatePasswordUiState(
                        isLoading = false,
                        errorMessage = body?.message ?: "No se pudo actualizar la contraseña (${resp.code()}) \n Debe tener mayusculas, minusculas y al menos un numero y un caracter especial"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CreatePasswordUiState(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error de red"
                )
            }
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
            try {
                val resp = ApiProvider.authApi.changePassword(
                    ChangePasswordDTO(
                        passwordNueva = newPassword,
                        correo = correo.trim(),
                        passwordActual = passwordActual
                    )
                )
                val body = resp.body()
                if (resp.isSuccessful && body?.error == false) {
                    _uiState.value = CreatePasswordUiState(
                        isLoading = false,
                        successMessage = body.message ?: "Contraseña actualizada"
                    )
                    onSuccess?.invoke()
                } else {
                    _uiState.value = CreatePasswordUiState(
                        isLoading = false,
                        errorMessage = body?.message ?: "No se pudo actualizar la contraseña (${resp.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = CreatePasswordUiState(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error de red"
                )
            }
        }
    }
}

