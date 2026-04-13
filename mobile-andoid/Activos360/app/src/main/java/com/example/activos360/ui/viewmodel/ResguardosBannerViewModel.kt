package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ResguardoRepository
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
    val isLoading: Boolean              = false,
    val items: List<ActivoBannerItem>   = emptyList()
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
                val pendientes = ResguardoRepository.findByEmpleado(userId)
                    .filter { r -> r.string("estadoResguardo")?.trim()?.lowercase() == "pendiente" }

                val items = pendientes.mapNotNull { r ->
                    val activo   = r["activo"].asMap() ?: return@mapNotNull null
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
