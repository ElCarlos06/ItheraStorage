package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ResguardoDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asListOfMaps
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
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
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida")

                val resguardoResp = ApiProvider.resguardoApi.findByActivo(activoId)
                val resguardosList = if (resguardoResp.isSuccessful) {
                    resguardoResp.body()?.data.asListOfMaps() ?: emptyList()
                } else emptyList()

                val confirmado = resguardosList.firstOrNull { r ->
                    val estado = r.string("estadoResguardo")?.trim()?.lowercase()
                    val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
                    estado == "confirmado" && empleadoId == userId
                } ?: throw IllegalStateException("No existe un resguardo confirmado para este activo")

                val resguardoId = confirmado.long("id")
                    ?: throw IllegalStateException("Resguardo sin id")
                val activoMap = confirmado["activo"].asMap()
                val empleadoMap = confirmado["usuarioEmpleado"].asMap()
                val adminMap = confirmado["usuarioAdmin"].asMap()

                val dto = ResguardoDTO(
                    idActivo = activoMap?.long("id") ?: activoId,
                    idUsuarioEmpleado = empleadoMap?.long("id") ?: userId,
                    idUsuarioAdmin = adminMap?.long("id") ?: error("Resguardo sin usuarioAdmin"),
                    estadoResguardo = "Devuelto",
                    observacionesDev = observaciones?.takeIf { it.isNotBlank() }
                )

                val updateResp = ApiProvider.resguardoApi.update5(resguardoId, dto)
                if (!updateResp.isSuccessful) {
                    throw IllegalStateException("No se pudo procesar la devolución (${updateResp.code()})")
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
