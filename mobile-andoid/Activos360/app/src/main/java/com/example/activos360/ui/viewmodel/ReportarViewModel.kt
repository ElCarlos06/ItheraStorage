package com.example.activos360.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.ReporteDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
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

    init {
        loadCatalogos()
    }

    private fun loadCatalogos() {
        viewModelScope.launch {
            try {
                val tiposResp = ApiProvider.tipoFallaApi.findAll1()
                val prioridadesResp = ApiProvider.prioridadApi.findAll7()

                val tiposList = parseTiposFalla(tiposResp.body()?.data)
                val prioridadesList = parsePrioridades(prioridadesResp.body()?.data)

                _uiState.value = _uiState.value.copy(
                    tiposFalla = tiposList,
                    prioridades = prioridadesList
                )
            } catch (_: Exception) {
                // Usar valores por defecto si falla la carga
                _uiState.value = _uiState.value.copy(
                    tiposFalla = emptyList(),
                    prioridades = emptyList()
                )
            }
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parseTiposFalla(data: Any?): List<TipoFallaItem> {
        if (data == null) return emptyList()
        val list = when (data) {
            is List<*> -> data
            is Map<*, *> -> (data["content"] as? List<*>) ?: emptyList<Any>()
            else -> emptyList<Any>()
        }
        return list.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            val id = (map["id"] as? Number)?.toLong() ?: return@mapNotNull null
            val nombre = map["nombre"] as? String ?: return@mapNotNull null
            TipoFallaItem(id, nombre)
        }
    }

    @Suppress("UNCHECKED_CAST")
    private fun parsePrioridades(data: Any?): List<PrioridadItem> {
        if (data == null) return emptyList()
        val list = when (data) {
            is List<*> -> data
            else -> emptyList<Any>()
        }
        return list.mapNotNull { item ->
            val map = item as? Map<*, *> ?: return@mapNotNull null
            val id = (map["id"] as? Number)?.toLong() ?: return@mapNotNull null
            val nivel = map["nivel"] as? String ?: return@mapNotNull null
            PrioridadItem(id, nivel)
        }
    }

    fun reportar(
        activoId: Long,
        idTipoFalla: Long,
        idPrioridad: Long,
        descripcionFalla: String,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(isLoading = true, errorMessage = null)
            try {
                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida")

                val dto = ReporteDTO(
                    idActivo = activoId,
                    idUsuarioReporta = userId,
                    idTipoFalla = idTipoFalla,
                    idPrioridad = idPrioridad,
                    descripcionFalla = descripcionFalla
                )

                val resp = ApiProvider.reporteApi.save6(dto)
                if (!resp.isSuccessful) {
                    throw IllegalStateException("No se pudo registrar el reporte (${resp.code()})")
                }

                _uiState.value = _uiState.value.copy(isLoading = false)
                onSuccess()
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al reportar daño"
                )
            }
        }
    }
}
