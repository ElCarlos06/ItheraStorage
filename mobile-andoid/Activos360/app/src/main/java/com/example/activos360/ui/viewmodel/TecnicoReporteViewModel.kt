package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.MantenimientoDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import java.util.TimeZone
import java.util.concurrent.TimeUnit

data class TecnicoReporteUiState(
    val isLoading: Boolean = true,
    val errorMessage: String? = null,
    // Activo (tab Información)
    val activoEtiqueta: String = "",
    val activoNombre: String = "",
    val activoNumeroSerie: String = "",
    val activoEstadoOperativo: String = "",
    val activoEstadoCustodia: String = "",
    // Campos de TipoActivo (marca y modelo están aquí en el back)
    val tipoActivoNombre: String = "",
    val tipoActivoMarca: String = "",
    val tipoActivoModelo: String = "",
    // Más campos del activo
    val activoCosto: String? = null,
    val activoDescripcion: String = "",
    val empleadoResguardante: String = "",
    // Reporte (tab Reporte)
    val tipoFalla: String = "",
    val prioridad: String = "",
    val reporterNombre: String = "",
    val reporterFotoUrl: String? = null,
    val fechaDisplay: String = "",
    val descripcion: String = "",
    val evidenciaUrls: List<String> = emptyList(),
    // Botón Atender / Cerrar
    val puedeAtender: Boolean = false,
    val enProceso: Boolean = false,
    val isAtendiendo: Boolean = false,
    val atenderError: String? = null
)

class TecnicoReporteViewModel : ViewModel() {

    var uiState by mutableStateOf(TecnicoReporteUiState())
        private set

    // Campos internos para reconstruir el DTO en atender()
    private var _mantenimientoId: Long = 0L
    private var _idReporte: Long = 0L
    private var _idActivo: Long = 0L
    private var _idTecnico: Long = 0L
    private var _idAdmin: Long = 0L
    private var _idPrioridad: Long = 0L
    private var _tipoAsignado: String = "Correctivo"

    fun load(activoId: Long, mantenimientoId: Long) {
        _mantenimientoId = mantenimientoId
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val activoDeferred       = async { ApiProvider.assetsApi.findById15(activoId) }
                val mantenimientoDeferred = async { ApiProvider.mantenimientoApi.findById10(mantenimientoId) }
                val resguardoDeferred    = async { ApiProvider.resguardoApi.findByActivo(activoId) }

                val activoResp        = activoDeferred.await()
                val mantenimientoResp = mantenimientoDeferred.await()
                val resguardoResp     = resguardoDeferred.await()

                val activoData = activoResp.body()?.data.asMap()
                val mtnData    = mantenimientoResp.body()?.data.asMap()

                // ── Activo — campos básicos ────────────────────────────────────
                val etiqueta    = activoData?.string("etiqueta")        ?: "Activo #$activoId"
                val nombre      = activoData?.string("nombre")          ?: etiqueta
                val numeroSerie = activoData?.string("numeroSerie")     ?: ""
                val estadoOp    = activoData?.string("estadoOperativo") ?: ""
                val estadoCus   = activoData?.string("estadoCustodia")  ?: ""
                val activoDescripcion = activoData?.string("descripcion") ?: ""

                // ── TipoActivo (marca y modelo viven aquí en el back) ─────────
                @Suppress("UNCHECKED_CAST")
                val tipoActivoMap    = activoData?.get("tipoActivo") as? Map<String, Any?>
                val tipoActivoNombre = tipoActivoMap?.string("nombre") ?: ""
                val tipoActivoMarca  = tipoActivoMap?.string("marca")  ?: ""
                val tipoActivoModelo = tipoActivoMap?.string("modelo") ?: ""

                // ── Costo ─────────────────────────────────────────────────────
                val costoDisplay = when (val c = activoData?.get("costo")) {
                    is Number -> "$%.2f".format(c.toDouble())
                    is String -> c.toDoubleOrNull()?.let { "$%.2f".format(it) }
                    else      -> null
                }

                // ── Empleado resguardante (primer resguardo confirmado) ────────
                val resguardoList: List<Map<String, Any?>> = if (resguardoResp.isSuccessful) {
                    val raw = resguardoResp.body()?.data
                    when (raw) {
                        is List<*> -> raw.filterIsInstance<Map<String, Any?>>()
                        is Map<*, *> -> (raw["content"] as? List<*>)
                            ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                        else -> emptyList()
                    }
                } else emptyList()

                @Suppress("UNCHECKED_CAST")
                val empleadoResguardante = resguardoList
                    .firstOrNull { r ->
                        val est = (r["estadoResguardo"] as? String)?.trim()?.lowercase()
                        est == "confirmado" || est == "resguardado"
                    }
                    ?.let { r ->
                        val emp = r["usuarioEmpleado"] as? Map<String, Any?>
                        (emp?.get("nombreCompleto") as? String)
                            ?: (emp?.get("nombre") as? String)
                    } ?: ""

                // ── ReporteId desde el mantenimiento ──
                val reporteMap = mtnData?.get("reporte").asMap()
                val reporteId = reporteMap?.long("id") ?: mtnData?.long("idReporte") ?: 0L

                // ── Campos internos para atender ──
                _idReporte = reporteId
                _idActivo = (mtnData?.get("activo").asMap())?.long("id") ?: mtnData?.long("idActivo") ?: activoId
                _idTecnico = (mtnData?.get("usuarioTecnico").asMap())?.long("id") ?: mtnData?.long("idUsuarioTecnico") ?: 0L
                _idAdmin = (mtnData?.get("usuarioAdmin").asMap())?.long("id") ?: mtnData?.long("idUsuarioAdmin") ?: 0L
                _idPrioridad = (mtnData?.get("prioridad").asMap())?.long("id") ?: mtnData?.long("idPrioridad") ?: 0L
                _tipoAsignado = mtnData?.string("tipoAsignado") ?: "Correctivo"

                val estadoMtn = mtnData?.string("estadoMantenimiento")?.lowercase() ?: ""

                // Verificar que el técnico logueado es el asignado a este mantenimiento.
                // Solo él puede ver "Atender" o "Cerrar mantenimiento"; cualquier otro técnico
                // que llegue a esta pantalla queda en modo solo-lectura.
                val tecnicoAsignadoId = (mtnData?.get("usuarioTecnico").asMap())?.long("id")
                    ?: mtnData?.long("idUsuarioTecnico")
                val currentUserId = TokenManager.getUserIdFromToken()
                val esElTecnicoAsignado = currentUserId != null && tecnicoAsignadoId == currentUserId

                val puedeAtender = estadoMtn !in listOf("en proceso", "completado", "cerrado", "finalizado") && esElTecnicoAsignado
                val enProceso = estadoMtn == "en proceso" && esElTecnicoAsignado

                // ── Reporte completo (llamada separada para obtener usuarioReporta) ──
                var tipoFalla = ""
                var prioridad = ""
                var reporterNombre = ""
                var reporterCorreo = ""
                var descripcion = ""
                var fechaRaw: Any? = null

                if (reporteId > 0L) {
                    try {
                        val reporteResp = ApiProvider.reporteApi.findById6(reporteId)
                        if (reporteResp.isSuccessful) {
                            val rd = reporteResp.body()?.data.asMap()
                            tipoFalla = (rd?.get("tipoFalla").asMap())?.string("nombre")
                                ?: rd?.string("tipoFalla") ?: ""
                            prioridad = (rd?.get("prioridad").asMap())?.string("nivel")
                                ?: rd?.string("prioridad") ?: ""
                            descripcion = rd?.string("descripcionFalla") ?: ""
                            // La fecha viene como array [year, month, day, h, m, s] desde Spring Boot
                            fechaRaw = rd?.get("fechaReporte")
                            val usuarioMap = rd?.get("usuarioReporta").asMap()
                            // El back serializa el campo como "nombreCompleto", no "nombre"
                            reporterNombre = (usuarioMap?.string("nombreCompleto")
                                ?: usuarioMap?.string("nombre"))
                                ?.split(" ")?.firstOrNull() ?: ""
                            reporterCorreo = usuarioMap?.string("correo") ?: ""
                        }
                    } catch (_: Exception) { }

                    // Fallback a datos del mtnData si el fetch del reporte falló
                    if (tipoFalla.isEmpty()) {
                        tipoFalla = (reporteMap?.get("tipoFalla").asMap())?.string("nombre") ?: ""
                    }
                    if (prioridad.isEmpty()) {
                        prioridad = (reporteMap?.get("prioridad").asMap())?.string("nivel") ?: ""
                    }
                    if (descripcion.isEmpty()) {
                        descripcion = reporteMap?.string("descripcionFalla") ?: ""
                    }
                    if (fechaRaw == null) {
                        fechaRaw = reporteMap?.get("fechaReporte")
                    }
                    if (reporterNombre.isEmpty()) {
                        val usuarioMap = reporteMap?.get("usuarioReporta").asMap()
                        reporterNombre = (usuarioMap?.string("nombreCompleto")
                            ?: usuarioMap?.string("nombre"))
                            ?.split(" ")?.firstOrNull() ?: ""
                        reporterCorreo = usuarioMap?.string("correo") ?: ""
                    }
                }

                // ── Foto del reportador ──
                var reporterFotoUrl: String? = null
                if (reporterCorreo.isNotBlank()) {
                    try {
                        val fotoResp = ApiProvider.imagenPerfilApi.obtenerImagen(reporterCorreo)
                        if (fotoResp.isSuccessful) {
                            reporterFotoUrl = urlFromData(fotoResp.body()?.data)
                        }
                    } catch (_: Exception) { }
                }

                // ── Evidencias del reporte ──
                var evidenciaUrls: List<String> = emptyList()
                if (reporteId > 0L) {
                    try {
                        val imgResp = ApiProvider.reporteApi.listarImagenes(reporteId)
                        if (imgResp.isSuccessful) {
                            val rawData = imgResp.body()?.data
                            val imgs: List<Map<String, Any?>> = when (rawData) {
                                is List<*> -> rawData.filterIsInstance<Map<String, Any?>>()
                                is Map<*, *> -> (rawData["content"] as? List<*>)
                                    ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                                else -> emptyList()
                            }
                            evidenciaUrls = imgs.mapNotNull { img ->
                                img.string("urlCloudinary")
                                    ?: img.string("url")
                                    ?: img.string("urlImagen")
                            }
                        }
                    } catch (_: Exception) { }

                    // Fallback: intentar con imagenReporteApi si el endpoint anterior no devuelve imágenes
                    if (evidenciaUrls.isEmpty()) {
                        try {
                            val imgResp2 = ApiProvider.imagenReporteApi.listarImagenes(reporteId)
                            if (imgResp2.isSuccessful) {
                                val rawData = imgResp2.body()?.data
                                val imgs: List<Map<String, Any?>> = when (rawData) {
                                    is List<*> -> rawData.filterIsInstance<Map<String, Any?>>()
                                    is Map<*, *> -> (rawData["content"] as? List<*>)
                                        ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                                    else -> emptyList()
                                }
                                evidenciaUrls = imgs.mapNotNull { img ->
                                    img.string("urlCloudinary")
                                        ?: img.string("url")
                                        ?: img.string("urlImagen")
                                }
                            }
                        } catch (_: Exception) { }
                    }
                }

                uiState = uiState.copy(
                    isLoading = false,
                    activoEtiqueta = etiqueta,
                    activoNombre = nombre,
                    activoNumeroSerie = numeroSerie,
                    activoEstadoOperativo = estadoOp,
                    activoEstadoCustodia = estadoCus,
                    tipoActivoNombre = tipoActivoNombre,
                    tipoActivoMarca = tipoActivoMarca,
                    tipoActivoModelo = tipoActivoModelo,
                    activoCosto = costoDisplay,
                    activoDescripcion = activoDescripcion,
                    empleadoResguardante = empleadoResguardante,
                    tipoFalla = tipoFalla,
                    prioridad = prioridad,
                    reporterNombre = reporterNombre,
                    reporterFotoUrl = reporterFotoUrl,
                    fechaDisplay = formatearFecha(fechaRaw),
                    descripcion = descripcion,
                    evidenciaUrls = evidenciaUrls,
                    puedeAtender = puedeAtender,
                    enProceso = enProceso
                )
            } catch (e: Exception) {
                uiState = uiState.copy(
                    isLoading = false,
                    errorMessage = e.localizedMessage ?: "Error al cargar datos"
                )
            }
        }
    }

    fun atender(onSuccess: () -> Unit = {}) {
        if (_mantenimientoId == 0L) return
        viewModelScope.launch {
            uiState = uiState.copy(isAtendiendo = true, atenderError = null)
            try {
                val dto = MantenimientoDTO(
                    idReporte = _idReporte,
                    idActivo = _idActivo,
                    idUsuarioTecnico = _idTecnico,
                    idUsuarioAdmin = _idAdmin,
                    idPrioridad = _idPrioridad,
                    tipoAsignado = _tipoAsignado,
                    estadoMantenimiento = "En Proceso"
                )
                val resp = ApiProvider.mantenimientoApi.update10(_mantenimientoId, dto)
                if (resp.isSuccessful) {
                    uiState = uiState.copy(isAtendiendo = false, puedeAtender = false, enProceso = true)
                    onSuccess()
                } else {
                    val msg = resp.errorBody()?.string()?.take(120) ?: "Error ${resp.code()}"
                    uiState = uiState.copy(isAtendiendo = false, atenderError = msg)
                }
            } catch (e: Exception) {
                uiState = uiState.copy(isAtendiendo = false, atenderError = e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun clearAtenderError() {
        uiState = uiState.copy(atenderError = null)
    }

    private fun urlFromData(data: Any?): String? = when (data) {
        is Map<*, *> -> (data["urlCloudinary"] as? String)
            ?: (data["url"] as? String)
            ?: (data["urlImagen"] as? String)
        is String -> data.takeIf { it.startsWith("http") }
        else -> null
    }

    /**
     * Convierte el campo fechaReporte a texto legible.
     * Spring Boot serializa LocalDateTime como array [year, month, day, hour, minute, second]
     * pero también puede venir como String ISO.
     */
    private fun formatearFecha(raw: Any?): String {
        if (raw == null) return ""
        val date: Date? = parsearFecha(raw)
        date ?: return raw.toString()

        val diffMs = Date().time - date.time
        val dias = TimeUnit.MILLISECONDS.toDays(diffMs)
        val horas = TimeUnit.MILLISECONDS.toHours(diffMs)
        val relativo = when {
            dias >= 2 -> "Hace $dias días"
            dias == 1L -> "Hace 1 día"
            horas >= 2 -> "Hace $horas horas"
            horas == 1L -> "Hace 1 hora"
            else -> "Hace unos minutos"
        }
        val fmtMostrar = SimpleDateFormat("dd MMM, HH:mm'h'", Locale("es", "MX"))
        return "$relativo (${fmtMostrar.format(date)})"
    }

    private fun parsearFecha(raw: Any?): Date? = when (raw) {
        // Array de Spring Boot: [year, month, day] o [year, month, day, hour, minute] o [year, month, day, hour, minute, second]
        is List<*> -> {
            val nums = raw.filterIsInstance<Number>()
            if (nums.size >= 3) {
                try {
                    // UTC porque Spring Boot serializa LocalDateTime en UTC.
                    // Al construir el Date en UTC, SimpleDateFormat convierte
                    // correctamente a la zona horaria local del dispositivo.
                    val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                    cal.set(Calendar.YEAR, nums[0].toInt())
                    cal.set(Calendar.MONTH, nums[1].toInt() - 1) // Calendar.MONTH es 0-indexed
                    cal.set(Calendar.DAY_OF_MONTH, nums[2].toInt())
                    cal.set(Calendar.HOUR_OF_DAY, if (nums.size > 3) nums[3].toInt() else 0)
                    cal.set(Calendar.MINUTE, if (nums.size > 4) nums[4].toInt() else 0)
                    cal.set(Calendar.SECOND, if (nums.size > 5) nums[5].toInt() else 0)
                    cal.set(Calendar.MILLISECOND, 0)
                    cal.time
                } catch (_: Exception) { null }
            } else null
        }
        // String ISO (Spring Boot puede enviar "2026-04-12T18:04:00" en UTC)
        is String -> {
            if (raw.isBlank()) null else {
                val formats = listOf(
                    "yyyy-MM-dd'T'HH:mm:ss.SSS",
                    "yyyy-MM-dd'T'HH:mm:ss",
                    "yyyy-MM-dd HH:mm:ss",
                    "yyyy-MM-dd"
                )
                val utc = TimeZone.getTimeZone("UTC")
                var date: Date? = null
                for (fmt in formats) {
                    try {
                        date = SimpleDateFormat(fmt, Locale.getDefault())
                            .apply { timeZone = utc }
                            .parse(raw)
                        if (date != null) break
                    } catch (_: Exception) { }
                }
                date
            }
        }
        // Epoch en milisegundos
        is Number -> Date(raw.toLong())
        else -> null
    }
}
