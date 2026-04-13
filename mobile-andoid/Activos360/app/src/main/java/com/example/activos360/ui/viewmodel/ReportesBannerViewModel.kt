package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.MantenimientoRepository
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.launch

data class ReportesBannerState(
    val isLoading: Boolean              = false,
    val items: List<ActivoBannerItem>   = emptyList()
) {
    val count: Int get() = items.size
}

class ReportesBannerViewModel : ViewModel() {

    var state by mutableStateOf(ReportesBannerState())
        private set

    fun cargar() {
        val userId = TokenManager.getUserIdFromToken() ?: return
        viewModelScope.launch {
            state = state.copy(isLoading = true)
            try {
                val pendientes = MantenimientoRepository.findByTecnico(userId)
                    .filter { m ->
                        val estado = m.string("estadoMantenimiento")?.trim()?.lowercase() ?: ""
                        estado !in listOf("finalizado", "completado", "cerrado", "cancelado")
                    }

                val items = pendientes.mapNotNull { m ->
                    val activo   = m["activo"].asMap() ?: return@mapNotNull null
                    val activoId = activo.long("id") ?: 0L
                    val etiqueta = activo.string("etiqueta").orEmpty().ifBlank { "—" }
                    val nombre   = activo["tipoActivo"].asMap()?.string("nombre").orEmpty().ifBlank { etiqueta }
                    val ubicacion = activo["espacio"].asMap()?.string("nombreEspacio").orEmpty().ifBlank { "—" }
                    ActivoBannerItem(activoId, nombre, etiqueta, ubicacion)
                }
                state = state.copy(isLoading = false, items = items)
            } catch (_: Exception) {
                state = state.copy(isLoading = false)
            }
        }
    }
}
