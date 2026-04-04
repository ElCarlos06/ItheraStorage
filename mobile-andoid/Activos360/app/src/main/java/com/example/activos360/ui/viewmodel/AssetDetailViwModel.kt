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
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asListOfMaps
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

data class AssetDetailUiState(
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val activo: Map<String, Any?>? = null,
    val resguardos: List<Map<String, Any?>> = emptyList(),
    val canResguardar: Boolean = false,
    val resguardoPendienteId: Long? = null,

    // --- LO QUE INVENTÓ CURSOR Y AHORA HACEMOS REALIDAD ---
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

    fun loadActivo(activoId: Long) {
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val activoDeferred = async { ApiProvider.assetsApi.findById15(activoId) }
                val resguardoDeferred = async { ApiProvider.resguardoApi.findByActivo(activoId) }

                val activoResp = activoDeferred.await()
                val resguardoResp = resguardoDeferred.await()

                val activoData = if (activoResp.isSuccessful) {
                    activoResp.body()?.data.asMap()
                } else null

                val resguardosList = if (resguardoResp.isSuccessful) {
                    resguardoResp.body()?.data.asListOfMaps() ?: emptyList()
                } else emptyList()

                val (canResguardar, resguardoPendienteId) = computeResguardar(activoData, resguardosList)

                uiState = uiState.copy(
                    isLoading = false,
                    activo = activoData,
                    resguardos = resguardosList,
                    canResguardar = canResguardar,
                    resguardoPendienteId = resguardoPendienteId
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

        // Backend en BD puede guardar "Disponible" / "En proceso" / "Resguardado" (o minúsculas).
        val estadoCustodia = activo?.string("estadoCustodia")?.trim()?.lowercase()

        val pendiente = resguardos
            .asSequence()
            .mapNotNull { r ->
                val estado = r.string("estadoResguardo")?.trim()?.lowercase()
                val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
                val resguardoId = r.long("id")
                if (estado == "pendiente" && empleadoId == userId && resguardoId != null) {
                    Triple(resguardoId, empleadoId, estado)
                } else null
            }
            .firstOrNull()

        // Regla de negocio solicitada: mostrar botón si "en proceso" y pertenece al empleado.
        // Interpretación práctica con el backend actual:
        // - "en proceso" puede venir como estadoCustodia == "en proceso"
        // - "pertenece" lo inferimos por Resguardo Pendiente para ese empleado.
        val enProceso = when {
            estadoCustodia == null -> false
            estadoCustodia == "en proceso" -> true
            estadoCustodia == "enproceso" -> true
            estadoCustodia == "proc" -> true
            estadoCustodia == "en_proceso" -> true
            estadoCustodia.contains("proceso") -> true
            estadoCustodia == "asignado" -> true
            else -> false
        }
        val can = enProceso && pendiente != null
        return can to pendiente?.first
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
                // Re-leer resguardos por si cambió mientras estaba en la pantalla
                val resguardoResp = ApiProvider.resguardoApi.findByActivo(activoId)
                val resguardosList = if (resguardoResp.isSuccessful) {
                    resguardoResp.body()?.data.asListOfMaps() ?: emptyList()
                } else emptyList()

                val userId = TokenManager.getUserIdFromToken()
                    ?: throw IllegalStateException("Sesión inválida: no se pudo leer el id del usuario")

                val pendiente = resguardosList.firstOrNull { r ->
                    val estado = r.string("estadoResguardo")?.trim()?.lowercase()
                    val empleadoId = (r["usuarioEmpleado"].asMap())?.long("id")
                    estado == "pendiente" && empleadoId == userId
                } ?: throw IllegalStateException("No existe un resguardo pendiente para este activo y usuario")

                val resguardoId = pendiente.long("id")
                    ?: throw IllegalStateException("El resguardo no trae id")

                val activoMap = pendiente["activo"].asMap()
                val empleadoMap = pendiente["usuarioEmpleado"].asMap()
                val adminMap = pendiente["usuarioAdmin"].asMap()

                val dto = ResguardoDTO(
                    idActivo = activoMap?.long("id") ?: activoId,
                    idUsuarioEmpleado = empleadoMap?.long("id") ?: userId,
                    idUsuarioAdmin = adminMap?.long("id") ?: error("Resguardo sin usuarioAdmin.id"),
                    estadoResguardo = "Confirmado",
                    observacionesConf = observaciones
                )

                val updateResp = ApiProvider.resguardoApi.update5(resguardoId, dto)
                if (!updateResp.isSuccessful) {
                    throw IllegalStateException("No se pudo confirmar el resguardo (${updateResp.code()})")
                }

                if (fotos.isNotEmpty() && context != null) {
                    subirFotosActivo(activoId, fotos, context)
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

    private suspend fun subirFotosActivo(activoId: Long, fotos: List<Uri>, context: Context) {
        fotos.forEachIndexed { index, uri ->
            try {
                val stream = context.contentResolver.openInputStream(uri) ?: return@forEachIndexed
                val bytes = stream.readBytes()
                stream.close()
                val requestBody = bytes.toRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part.createFormData("file", "foto_${index + 1}.jpg", requestBody)
                ApiProvider.imagenActivoApi.subirImagen(activoId, part)
            } catch (_: Exception) { }
        }
    }
 }

