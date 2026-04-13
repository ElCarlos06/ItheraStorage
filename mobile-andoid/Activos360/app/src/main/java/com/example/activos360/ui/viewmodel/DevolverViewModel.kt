package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ResguardoDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ActivoRepository
import com.example.activos360.core.repository.ResguardoRepository
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class DevolverUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null
)

class DevolverViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(DevolverUiState())
    val uiState: StateFlow<DevolverUiState> = _uiState.asStateFlow()

    fun devolver(
        activoId: Long,
        observaciones: String?,
        fotos: List<Uri> = emptyList(),
        context: Context? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida")

                val confirmado = ResguardoRepository.findConfirmadoParaUsuario(activoId, userId)
                    ?: throw IllegalStateException("No existe un resguardo confirmado para este activo")

                val resguardoId = confirmado.long("id")
                    ?: throw IllegalStateException("Resguardo sin id")

                val activoMap   = confirmado["activo"].asMap()
                val empleadoMap = confirmado["usuarioEmpleado"].asMap()
                val adminMap    = confirmado["usuarioAdmin"].asMap()

                val dto = ResguardoDTO(
                    idActivo          = activoMap?.long("id") ?: activoId,
                    idUsuarioEmpleado = empleadoMap?.long("id") ?: userId,
                    idUsuarioAdmin    = adminMap?.long("id") ?: error("Resguardo sin usuarioAdmin"),
                    estadoResguardo   = "Devuelto",
                    observacionesDev  = observaciones?.takeIf { it.isNotBlank() }
                )

                ResguardoRepository.devolver(resguardoId, dto)

                if (fotos.isNotEmpty() && context != null) {
                    ActivoRepository.subirImagenes(activoId, fotos, context)
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al devolver activo"
                )
            }
        }
    }
}
