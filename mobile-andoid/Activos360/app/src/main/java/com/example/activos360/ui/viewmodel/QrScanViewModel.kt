package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ActivoRepository
import com.example.activos360.core.repository.MantenimientoRepository
import com.example.activos360.core.util.QrParse
import com.example.activos360.core.util.long
import kotlinx.coroutines.launch

sealed class QrScanResult {
    object Idle               : QrScanResult()
    object QrInvalido         : QrScanResult()
    object ActivoDadoDeBaja   : QrScanResult()
    data class AbrirModal(val codigo: String)                               : QrScanResult()
    data class NavegarDirecto(val activoId: Long, val mantenimientoId: Long): QrScanResult()
}

data class QrScanUiState(
    val isLoading: Boolean    = false,
    val result: QrScanResult  = QrScanResult.Idle
)

class QrScanViewModel : ViewModel() {

    var uiState by mutableStateOf(QrScanUiState())
        private set

    fun procesarEmpleado(codigo: String) {
        viewModelScope.launch {
            uiState = QrScanUiState(isLoading = true)

            if (!QrParse.isActivoQrFormat(codigo)) {
                uiState = QrScanUiState(result = QrScanResult.QrInvalido)
                return@launch
            }

            val activoId = try { QrParse.resolveActivoId(codigo) ?: 0L } catch (_: Exception) { 0L }

            if (activoId > 0L && ActivoRepository.isBaja(activoId)) {
                uiState = QrScanUiState(result = QrScanResult.ActivoDadoDeBaja)
                return@launch
            }

            uiState = QrScanUiState(result = QrScanResult.AbrirModal(codigo))
        }
    }

    fun procesarTecnico(codigo: String) {
        viewModelScope.launch {
            uiState = QrScanUiState(isLoading = true)

            if (!QrParse.isActivoQrFormat(codigo)) {
                uiState = QrScanUiState(result = QrScanResult.QrInvalido)
                return@launch
            }

            val activoId = try { QrParse.resolveActivoId(codigo) ?: 0L } catch (_: Exception) { 0L }

            if (activoId > 0L && ActivoRepository.isBaja(activoId)) {
                uiState = QrScanUiState(result = QrScanResult.ActivoDadoDeBaja)
                return@launch
            }

            val userId = TokenManager.getUserIdFromToken()
            val miMantenimiento = if (activoId > 0L && userId != null) {
                try { MantenimientoRepository.findActivoParaTecnico(activoId, userId) }
                catch (_: Exception) { null }
            } else null

            if (miMantenimiento != null) {
                val mantenimientoId = (miMantenimiento["id"] as? Number)?.toLong() ?: 0L
                if (mantenimientoId > 0L) {
                    uiState = QrScanUiState(result = QrScanResult.NavegarDirecto(activoId, mantenimientoId))
                    return@launch
                }
            }

            uiState = QrScanUiState(result = QrScanResult.AbrirModal(codigo))
        }
    }

    fun resetResult() {
        uiState = QrScanUiState()
    }
}
