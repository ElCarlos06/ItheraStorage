package com.example.activos360.ui.viewmodel

import android.util.Log
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
                val tiposList = parseTiposFalla(tiposResp.body()?.data)
                Log.d("REPORTE_DEBUG", "tiposFalla cargados: ${tiposList.size} → $tiposList")
                _uiState.value = _uiState.value.copy(tiposFalla = tiposList)
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Error cargando tiposFalla: ${e.message}")
            }

            try {
                val prioridadesResp = ApiProvider.prioridadApi.findAll7()
                Log.d("REPORTE_DEBUG", "prioridades HTTP ${prioridadesResp.code()}, body null=${prioridadesResp.body() == null}, data=${prioridadesResp.body()?.data?.javaClass?.simpleName}: ${prioridadesResp.body()?.data}")
                val prioridadesList = parsePrioridades(prioridadesResp.body()?.data)
                Log.d("REPORTE_DEBUG", "prioridades parseadas: ${prioridadesList.size} → $prioridadesList")
                _uiState.value = _uiState.value.copy(prioridades = prioridadesList)
            } catch (e: Exception) {
                Log.e("REPORTE_DEBUG", "Error cargando prioridades: ${e.message}")
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
            is Map<*, *> -> (data["content"] as? List<*>) ?: emptyList<Any>()
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
        tipoNombre: String,
        prioridadNivel: String,
        descripcionFalla: String,
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
                    ?: throw IllegalStateException("Prioridad no reconocida: $prioridadNivel (disponibles: ${_uiState.value.prioridades.map { it.nivel }})")

                val dto = ReporteDTO(
                    idActivo = activoId,
                    idUsuarioReporta = userId,
                    idTipoFalla = idTipoFalla,
                    idPrioridad = idPrioridad,
                    descripcionFalla = descripcionFalla
                )

                Log.d("REPORTE_DEBUG", "Enviando DTO: $dto")

                val resp = ApiProvider.reporteApi.save6(dto)

                if (resp.isSuccessful) {
                    _uiState.value = _uiState.value.copy(isLoading = false)
                    onSuccess()
                } else {
                    val errorBody = resp.errorBody()?.string()
                    Log.e("REPORTE_DEBUG", "Error del backend: $errorBody")
                    throw IllegalStateException("Error ${resp.code()}: No se pudo registrar")
                }
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
