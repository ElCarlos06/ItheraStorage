package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

data class ForgotPasswordUiState(
    val isLoading: Boolean       = false,
    val errorMessage: String?    = null,
    val successMessage: String?  = null
)

class ForgotPasswordViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ForgotPasswordUiState())
    val uiState: StateFlow<ForgotPasswordUiState> = _uiState

    fun requestReset(correo: String) {
        viewModelScope.launch {
            _uiState.value = ForgotPasswordUiState(isLoading = true)
            val result = AuthRepository.requestPasswordReset(correo)
            result.fold(
                onSuccess = { _uiState.value = ForgotPasswordUiState(successMessage = it) },
                onFailure = { _uiState.value = ForgotPasswordUiState(errorMessage = it.localizedMessage ?: "Error de red") }
            )
        }
    }
}
