package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asListOfMaps
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.launch

data class ActivoBannerItem(
    val activoId: Long,
    val nombre: String,
    val etiqueta: String,
    val ubicacion: String
)

data class ResguardosBannerState(
    val isLoading: Boolean = false,
    val items: List<ActivoBannerItem> = emptyList()
) {
    val count: Int get() = items.size
}

class ResguardosBannerViewModel : ViewModel() {

    var state by mutableStateOf(ResguardosBannerState())
        private set

    fun cargar() {
        val userId = TokenManager.getUserIdFromToken() ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val resp = ApiProvider.resguardoApi.findByEmpleado(userId)
                if (resp.isSuccessful) {
                    val rawData = resp.body()?.data
                    val list: List<Map<String, Any?>> = when (rawData) {
                        is List<*> -> rawData.filterIsInstance<Map<String, Any?>>()
                        is Map<*, *> -> (rawData["content"] as? List<*>)
                            ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                        else -> emptyList()
                    }

                    val pendientes = list.filter { r ->
                        val estado = r.string("estadoResguardo")?.trim()?.lowercase()
                        estado == "pendiente"
                    }

                    val items = pendientes.mapNotNull { r ->
                        val activo = r["activo"].asMap() ?: return@mapNotNull null
                        val activoId = activo.long("id") ?: 0L
                        val etiqueta = activo.string("etiqueta").orEmpty().ifBlank { "—" }
                        val tipoActivo = activo["tipoActivo"].asMap()
                        val nombre = tipoActivo?.string("nombre").orEmpty().ifBlank { etiqueta }
                        val espacio = activo["espacio"].asMap()
                        val ubicacion = espacio?.string("nombreEspacio").orEmpty().ifBlank { "—" }
                        ActivoBannerItem(activoId, nombre, etiqueta, ubicacion)
                    }

                    state = state.copy(isLoading = false, items = items)
                } else {
                    state = state.copy(isLoading = false)
                }
            } catch (_: Exception) {
                state = state.copy(isLoading = false)
            }
        }
    }
}
