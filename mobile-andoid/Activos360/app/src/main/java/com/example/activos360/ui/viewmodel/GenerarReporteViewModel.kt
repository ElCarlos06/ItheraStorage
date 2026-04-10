package com.example.activos360.ui.viewmodel

import android.content.Context
import android.net.Uri
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.AssetsDTO
import com.example.activos360.back.model.MantenimientoDTO
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class GenerarReporteUiState(
    val isLoading: Boolean = true,
    val activoEtiqueta: String = "",
    val activoNombre: String = "",
    val isGuardando: Boolean = false,
    val errorMessage: String? = null,
    val exito: Boolean = false
)

class GenerarReporteViewModel : ViewModel() {

    var uiState by mutableStateOf(GenerarReporteUiState())
        private set

    // Campos del activo necesarios para reconstruir AssetsDTO en "baja"
    private var _activoId: Long = 0L
    private var _etiqueta: String = ""
    private var _numeroSerie: String = ""
    private var _idTipoActivo: Long = 0L
    private var _idModelo: Long = 0L
    private var _idEspacio: Long = 0L
    private var _estadoCustodia: String? = null
    private var _descripcion: String? = null

    // Campos del mantenimiento para reconstruir MantenimientoDTO
    private var _mantenimientoId: Long = 0L
    private var _idReporte: Long = 0L
    private var _idTecnico: Long = 0L
    private var _idAdmin: Long = 0L
    private var _idPrioridad: Long = 0L
    private var _tipoAsignado: String = "Correctivo"

    fun load(activoId: Long, mantenimientoId: Long) {
        _activoId = activoId
        _mantenimientoId = mantenimientoId
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val activoResp = ApiProvider.assetsApi.findById15(activoId)
                val mtnResp = ApiProvider.mantenimientoApi.findById10(mantenimientoId)

                val activoData = activoResp.body()?.data.asMap()
                val mtnData = mtnResp.body()?.data.asMap()

                // Activo
                _etiqueta = activoData?.string("etiqueta") ?: ""
                _numeroSerie = activoData?.string("numeroSerie") ?: ""
                _idTipoActivo = (activoData?.get("tipoActivo").asMap())?.long("id")
                    ?: activoData?.long("idTipoActivo") ?: 0L
                _idModelo = (activoData?.get("modelo").asMap())?.long("id")
                    ?: activoData?.long("idModelo") ?: 0L
                _idEspacio = (activoData?.get("espacio").asMap())?.long("id")
                    ?: activoData?.long("idEspacio") ?: 0L
                _estadoCustodia = activoData?.string("estadoCustodia")
                _descripcion = activoData?.string("descripcion")

                // Mantenimiento
                _idReporte = (mtnData?.get("reporte").asMap())?.long("id")
                    ?: mtnData?.long("idReporte") ?: 0L
                _idTecnico = (mtnData?.get("usuarioTecnico").asMap())?.long("id")
                    ?: mtnData?.long("idUsuarioTecnico") ?: 0L
                _idAdmin = (mtnData?.get("usuarioAdmin").asMap())?.long("id")
                    ?: mtnData?.long("idUsuarioAdmin") ?: 0L
                _idPrioridad = (mtnData?.get("prioridad").asMap())?.long("id")
                    ?: mtnData?.long("idPrioridad") ?: 0L
                _tipoAsignado = mtnData?.string("tipoAsignado") ?: "Correctivo"

                uiState = uiState.copy(
                    isLoading = false,
                    activoEtiqueta = _etiqueta.ifBlank { "ACTIVO #$activoId" },
                    activoNombre = activoData?.string("nombre") ?: _etiqueta
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al cargar"
                )
            }
        }
    }

    fun cerrarMantenimiento(
        diagnostico: String,
        accionesRealizadas: String,
        piezasUtilizadas: String,
        observaciones: String,
        resultado: String, // "Reparado" o "Irreparable"
        fotos: List<Uri> = emptyList(),
        context: Context? = null,
        onSuccess: () -> Unit
    ) {
        viewModelScope.launch {
            uiState = uiState.copy(isGuardando = true, errorMessage = null)
            try {
                val dto = MantenimientoDTO(
                    idReporte = _idReporte,
                    idActivo = _activoId,
                    idUsuarioTecnico = _idTecnico,
                    idUsuarioAdmin = _idAdmin,
                    idPrioridad = _idPrioridad,
                    tipoAsignado = _tipoAsignado,
                    estadoMantenimiento = "Finalizado",
                    diagnostico = diagnostico.ifBlank { null },
                    accionesRealizadas = accionesRealizadas.ifBlank { null },
                    piezasUtilizadas = piezasUtilizadas.ifBlank { null },
                    observaciones = observaciones.ifBlank { null },
                    conclusion = resultado
                )

                val resp = ApiProvider.mantenimientoApi.update10(_mantenimientoId, dto)
                if (!resp.isSuccessful) {
                    uiState = uiState.copy(
                        isGuardando = false,
                        errorMessage = "Error al cerrar mantenimiento: ${resp.code()}"
                    )
                    return@launch
                }

                // Subir fotos de evidencia del mantenimiento
                if (fotos.isNotEmpty() && context != null) {
                    fotos.forEachIndexed { index, uri ->
                        try {
                            val stream = context.contentResolver.openInputStream(uri) ?: return@forEachIndexed
                            val bytes = stream.readBytes(); stream.close()
                            val requestBody = bytes.toRequestBody("image/*".toMediaType())
                            val part = MultipartBody.Part.createFormData("file", "evidencia_${index + 1}.jpg", requestBody)
                            ApiProvider.imagenActivoApi.subirImagen(_activoId, part)
                        } catch (_: Exception) { }
                    }
                }

                // Si es irreparable, marcar activo como "Baja"
                if (resultado == "Irreparable" && _idTipoActivo > 0L && _idModelo > 0L && _idEspacio > 0L) {
                    try {
                        val activoDto = AssetsDTO(
                            etiqueta = _etiqueta,
                            numeroSerie = _numeroSerie,
                            idTipoActivo = _idTipoActivo,
                            idModelo = _idModelo,
                            idEspacio = _idEspacio,
                            estadoCustodia = _estadoCustodia,
                            estadoOperativo = "Baja",
                            descripcion = _descripcion,
                            esActivo = false
                        )
                        ApiProvider.assetsApi.update15(_activoId, activoDto)
                    } catch (_: Exception) { }
                }

                uiState = uiState.copy(isGuardando = false, exito = true)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isGuardando = false,
                    errorMessage = e.localizedMessage ?: "Error inesperado"
                )
            }
        }
    }

    fun clearError() {
        uiState = uiState.copy(errorMessage = null)
    }
}
