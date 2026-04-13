package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.repository.AuthRepository
import com.example.activos360.core.repository.LoginResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import java.net.URLEncoder

class LoginViewModel : ViewModel() {

    var isLoading    by mutableStateOf(false)
    var loggedRole   by mutableStateOf<String?>(null)
        private set
    var errorMessage by mutableStateOf<String?>(null)

    var usuarioLogueadoCorreo by mutableStateOf("")
    var usuarioLogueadoNombre by mutableStateOf("Usuario")

    private val _navegacionDestino = MutableStateFlow<String?>(null)
    val navegacionDestino: StateFlow<String?> = _navegacionDestino

    fun performLogin(email: String, pass: String) {
        viewModelScope.launch {
            isLoading    = true
            errorMessage = null
            when (val result = AuthRepository.login(email, pass)) {
                is LoginResult.Success -> {
                    loggedRole = result.role
                    _navegacionDestino.value = result.destino
                }
                is LoginResult.RequiresPasswordChange -> {
                    _navegacionDestino.value =
                        "create_password?correo=${URLEncoder.encode(result.correo, "UTF-8")}"
                }
                is LoginResult.Error -> {
                    errorMessage = result.message
                }
            }
            isLoading = false
        }
    }

    fun navegacionCompletada() {
        _navegacionDestino.value = null
    }
}
