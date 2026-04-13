package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ResguardoDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ActivoRepository
import com.example.activos360.core.repository.ResguardoRepository
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.async
import kotlinx.coroutines.launch

data class AssetDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activo: Map<String, Any?>? = null,
    val resguardos: List<Map<String, Any?>> = emptyList(),
    val canResguardar: Boolean = false,
    val resguardoPendienteId: Long? = null,
    val esResguardadoPorMiUsuario: Boolean = false,
    val isVisible: Boolean = false,
    val idEtiqueta: String = ""
)

class AssetDetailViewModel : ViewModel() {

    var uiState by mutableStateOf(AssetDetailUiState())
        private set

    fun showModal(codigoQr: String) {
        uiState = uiState.copy(isVisible = true, idEtiqueta = codigoQr)
    }

    fun dismissModal() {
        uiState = uiState.copy(isVisible = false, idEtiqueta = "")
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }

    fun loadActivo(activoId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val activoDeferred    = async { ActivoRepository.findById(activoId) }
                val resguardoDeferred = async { ResguardoRepository.findByActivo(activoId) }

                val activoData    = activoDeferred.await()
                val resguardosList = resguardoDeferred.await()

                val (canResguardar, resguardoPendienteId) = computeResguardar(activoData, resguardosList)
                val esResguardadoPorMiUsuario = computeEsResguardadoPorMiUsuario(resguardosList)

                uiState = uiState.copy(
                    isLoading = false,
                    activo = activoData,
                    resguardos = resguardosList,
                    canResguardar = canResguardar,
                    resguardoPendienteId = resguardoPendienteId,
                    esResguardadoPorMiUsuario = esResguardadoPorMiUsuario
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error de conexión"
                )
            }
        }
    }

    private fun computeResguardar(
        activo: Map<String, Any?>?,
        resguardos: List<Map<String, Any?>>
    ): Pair<Boolean, Long?> {
        val userId = TokenManager.getUserIdFromToken() ?: return false to null
        val estadoCustodia = activo?.string("estadoCustodia")?.trim()?.lowercase()

        val pendiente = resguardos.asSequence().mapNotNull { r ->
            val estado     = r.string("estadoResguardo")?.trim()?.lowercase()
            val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
            val resguardoId = r.long("id")
            if (estado == "pendiente" && empleadoId == userId && resguardoId != null)
                Triple(resguardoId, empleadoId, estado) else null
        }.firstOrNull()

        val enProceso = when {
            estadoCustodia == null -> false
            estadoCustodia.contains("proceso") -> true
            estadoCustodia == "asignado" -> true
            else -> false
        }
        return (enProceso && pendiente != null) to pendiente?.first
    }

    private fun computeEsResguardadoPorMiUsuario(resguardos: List<Map<String, Any?>>): Boolean {
        val userId = TokenManager.getUserIdFromToken() ?: return false
        return resguardos.any { r ->
            val estado     = r.string("estadoResguardo")?.trim()?.lowercase()
            val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
            (estado == "confirmado" || estado == "resguardado") && empleadoId == userId
        }
    }

    fun confirmarResguardo(
        activoId: Long,
        observaciones: String? = null,
        fotos: List<Uri> = emptyList(),
        context: Context? = null,
        onSuccess: (() -> Unit)? = null
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida")

                val pendiente = ResguardoRepository.findPendienteParaUsuario(activoId, userId)
                    ?: throw IllegalStateException("No existe un resguardo pendiente para este activo y usuario")

                val resguardoId = pendiente.long("id")
                    ?: throw IllegalStateException("El resguardo no trae id")

                val activoMap   = pendiente["activo"].asMap()
                val empleadoMap = pendiente["usuarioEmpleado"].asMap()
                val adminMap    = pendiente["usuarioAdmin"].asMap()

                val dto = ResguardoDTO(
                    idActivo           = activoMap?.long("id") ?: activoId,
                    idUsuarioEmpleado  = empleadoMap?.long("id") ?: userId,
                    idUsuarioAdmin     = adminMap?.long("id") ?: error("Resguardo sin usuarioAdmin.id"),
                    estadoResguardo    = "Confirmado",
                    observacionesConf  = observaciones
                )

                ResguardoRepository.confirmar(resguardoId, dto)

                if (fotos.isNotEmpty() && context != null) {
                    ActivoRepository.subirImagenes(activoId, fotos, context)
                }

                uiState = uiState.copy(isLoading = false)
                onSuccess?.invoke()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al confirmar resguardo"
                )
            }
        }
    }
}
