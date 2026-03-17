package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.launch

class ChangePasswordViewModel : ViewModel() {
    // Estados de la UI
    var isLoading by mutableStateOf(false)
        private set
    var errorMessage by mutableStateOf<String?>(null)
        private set
    var isSuccess by mutableStateOf(false)
        private set

    fun changePassword(assignedPassword: String, newPassword: String) {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            isSuccess = false

            try {
                // AQUÍ VA TU LLAMADA AL BACKEND REAL
                // Ejemplo con Retrofit:
                // val response = repository.updatePassword(assignedPassword, newPassword)
                // if (response.isSuccessful) { ... }

                // --- Simulación de backend (BORRAR ESTO) ---
                kotlinx.coroutines.delay(2000)
                if (assignedPassword.isEmpty() || newPassword.isEmpty()) {
                    throw Exception("Las contraseñas no pueden estar vacías")
                }
                // --- Fin de simulación ---

                // Si el backend responde OK:
                isSuccess = true
            } catch (e: Exception) {
                // Si el backend manda error o no hay internet
                errorMessage = e.message ?: "Error al actualizar la contraseña"
            } finally {
                isLoading = false
            }
        }
    }
}