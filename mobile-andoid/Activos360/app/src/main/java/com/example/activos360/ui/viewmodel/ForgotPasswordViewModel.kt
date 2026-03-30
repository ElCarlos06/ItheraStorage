package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.RequestPasswordResetDTO
import com.example.activos360.core.network.ApiProvider
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val successMessage: String? = null
)

class ForgotPasswordViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun requestReset(correo: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState(isLoading = true)
            try {
                val resp = ApiProvider.authApi.requestPasswordReset(
                    RequestPasswordResetDTO(correo = correo.trim())
                )
                val body = resp.body()
                if (resp.isSuccessful && body?.error == false) {
                    _uiState.value = ForgotPasswordUiState(
                        successMessage = body.message ?: "Revisa tu correo para restablecer tu contraseña"
                    )
                } else {
                    _uiState.value = ForgotPasswordUiState(
                        errorMessage = body?.message ?: "No se pudo enviar el correo (${resp.code()})"
                    )
                }
            } catch (e: Exception) {
                _uiState.value = ForgotPasswordUiState(
                    errorMessage = e.localizedMessage ?: "Error de red"
                )
            }
        }
    }
}
