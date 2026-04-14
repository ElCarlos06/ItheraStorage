package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ReporteDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ReporteRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class TipoFallaItem(val id: Long, val nombre: String)
data class PrioridadItem(val id: Long, val nivel: String)

data class ReportarUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val tiposFalla: List<TipoFallaItem> = emptyList(),
    val prioridades: List<PrioridadItem> = emptyList()
)

class ReportarViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(ReportarUiState())
    val uiState: StateFlow<ReportarUiState> = _uiState.asStateFlow()

    init { loadCatalogos() }

    private fun loadCatalogos() {
        viewModelScope.launch {
            try {
                val tiposList = ReporteRepository.loadTiposFalla()
                    .map { (id, nombre) -> TipoFallaItem(id, nombre) }
                Log.d("REPORTE_DEBUG", "tiposFalla cargados: ${tiposList.size}")
                _uiState.value = _uiState.value.copy(tiposFalla = tiposList)
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Error cargando tiposFalla: ${e.message}")
            }
            try {
                val prioridadesList = ReporteRepository.loadPrioridades()
                    .map { (id, nivel) -> PrioridadItem(id, nivel) }
                Log.d("REPORTE_DEBUG", "prioridades cargadas: ${prioridadesList.size}")
                _uiState.value = _uiState.value.copy(prioridades = prioridadesList)
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Error cargando prioridades: ${e.message}")
            }
        }
    }

    fun reportar(
        activoId: Long,
        tipoNombre: String,
        prioridadNivel: String,
        descripcionFalla: String,
        fotos: List<Uri> = emptyList(),
        context: Context? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida")

                val idTipoFalla = _uiState.value.tiposFalla.find { it.nombre == tipoNombre }?.id
                    ?: throw IllegalStateException("Tipo de falla no reconocido: $tipoNombre")

                val idPrioridad = _uiState.value.prioridades.find { it.nivel == prioridadNivel }?.id
                    ?: throw IllegalStateException("Prioridad no reconocida: $prioridadNivel")

                val dto = ReporteDTO(
                    idActivo         = activoId,
                    idUsuarioReporta = userId,
                    idTipoFalla      = idTipoFalla,
                    idPrioridad      = idPrioridad,
                    descripcionFalla = descripcionFalla
                )

                // Resuelve reportes colgados de mantenimientos ya finalizados
                ReporteRepository.resolverAbiertos(activoId)

                Log.d("REPORTE_DEBUG", "Enviando DTO: $dto")

                val reporteId = ReporteRepository.save(dto)

                if (reporteId != null && fotos.isNotEmpty() && context != null) {
                    ReporteRepository.subirImagenes(reporteId, fotos, context)
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Excepción: ${e.message}")
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al reportar daño"
                )
            }
        }
    }
}
