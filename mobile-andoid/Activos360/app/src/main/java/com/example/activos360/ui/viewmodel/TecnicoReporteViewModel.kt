package com.example.activos360.ui.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.activos360.back.model.MantenimientoDTO
import com.example.activos360.core.auth.TokenManager
import com.example.activos360.core.repository.ActivoRepository
import com.example.activos360.core.repository.MantenimientoRepository
import com.example.activos360.core.repository.PerfilRepository
import com.example.activos360.core.repository.ReporteRepository
import com.example.activos360.core.repository.ResguardoRepository
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
    val activoEtiqueta: String = "",
    val activoNombre: String = "",
    val activoNumeroSerie: String = "",
    val activoEstadoOperativo: String = "",
    val activoEstadoCustodia: String = "",
    val tipoActivoNombre: String = "",
    val tipoActivoMarca: String = "",
    val tipoActivoModelo: String = "",
    val activoCosto: String? = null,
    val activoDescripcion: String = "",
    val empleadoResguardante: String = "",
    val tipoFalla: String = "",
    val prioridad: String = "",
    val reporterNombre: String = "",
    val reporterFotoUrl: String? = null,
    val fechaDisplay: String = "",
    val descripcion: String = "",
    val evidenciaUrls: List<String> = emptyList(),
    val puedeAtender: Boolean = false,
    val enProceso: Boolean = false,
    val isAtendiendo: Boolean = false,
    val atenderError: String? = null
)

class TecnicoReporteViewModel : ViewModel() {

    var uiState by mutableStateOf(TecnicoReporteUiState())
        private set

    private var _mantenimientoId: Long = 0L
    private var _idReporte: Long       = 0L
    private var _idActivo: Long        = 0L
    private var _idTecnico: Long       = 0L
    private var _idAdmin: Long         = 0L
    private var _idPrioridad: Long     = 0L
    private var _tipoAsignado: String  = "Correctivo"

    fun load(activoId: Long, mantenimientoId: Long) {
        _mantenimientoId = mantenimientoId
        viewModelScope.launch {
            uiState = uiState.copy(isLoading = true, errorMessage = null)
            try {
                val activoDeferred        = async { ActivoRepository.findById(activoId) }
                val mantenimientoDeferred = async { MantenimientoRepository.findById(mantenimientoId) }
                val resguardoDeferred     = async { ResguardoRepository.findByActivo(activoId) }

                val activoData    = activoDeferred.await()
                val mtnData       = mantenimientoDeferred.await()
                val resguardoList = resguardoDeferred.await()

                // ── Activo ────────────────────────────────────────────────────────
                val etiqueta    = activoData?.string("etiqueta")        ?: "Activo #$activoId"
                val nombre      = activoData?.string("nombre")          ?: etiqueta
                val numeroSerie = activoData?.string("numeroSerie")     ?: ""
                val estadoOp    = activoData?.string("estadoOperativo") ?: ""
                val estadoCus   = activoData?.string("estadoCustodia")  ?: ""
                val activoDesc  = activoData?.string("descripcion")     ?: ""

                @Suppress("UNCHECKED_CAST")
                val tipoActivoMap    = activoData?.get("tipoActivo") as? Map<String, Any?>
                val tipoActivoNombre = tipoActivoMap?.string("nombre") ?: ""
                val tipoActivoMarca  = tipoActivoMap?.string("marca")  ?: ""
                val tipoActivoModelo = tipoActivoMap?.string("modelo") ?: ""

                val costoDisplay = when (val c = activoData?.get("costo")) {
                    is Number -> "$%.2f".format(c.toDouble())
                    is String -> c.toDoubleOrNull()?.let { "$%.2f".format(it) }
                    else      -> null
                }

                // ── Empleado resguardante ─────────────────────────────────────────
                @Suppress("UNCHECKED_CAST")
                val empleadoResguardante = resguardoList
                    .firstOrNull { r ->
                        val est = (r["estadoResguardo"] as? String)?.trim()?.lowercase()
                        est == "confirmado" || est == "resguardado"
                    }?.let { r ->
                        val emp = r["usuarioEmpleado"] as? Map<String, Any?>
                        (emp?.get("nombreCompleto") as? String) ?: (emp?.get("nombre") as? String)
                    } ?: ""

                // ── Campos internos para atender ──────────────────────────────────
                val reporteMap = mtnData?.get("reporte").asMap()
                val reporteId  = reporteMap?.long("id") ?: mtnData?.long("idReporte") ?: 0L

                _idReporte    = reporteId
                _idActivo     = (mtnData?.get("activo").asMap())?.long("id") ?: mtnData?.long("idActivo") ?: activoId
                _idTecnico    = (mtnData?.get("usuarioTecnico").asMap())?.long("id") ?: mtnData?.long("idUsuarioTecnico") ?: 0L
                _idAdmin      = (mtnData?.get("usuarioAdmin").asMap())?.long("id") ?: mtnData?.long("idUsuarioAdmin") ?: 0L
                _idPrioridad  = (mtnData?.get("prioridad").asMap())?.long("id") ?: mtnData?.long("idPrioridad") ?: 0L
                _tipoAsignado = mtnData?.string("tipoAsignado") ?: "Correctivo"

                val estadoMtn          = mtnData?.string("estadoMantenimiento")?.lowercase() ?: ""
                val tecnicoAsignadoId  = (mtnData?.get("usuarioTecnico").asMap())?.long("id") ?: mtnData?.long("idUsuarioTecnico")
                val currentUserId      = TokenManager.getUserIdFromToken()
                val esElTecnico        = currentUserId != null && tecnicoAsignadoId == currentUserId
                val puedeAtender       = estadoMtn !in listOf("en proceso", "completado", "cerrado", "finalizado") && esElTecnico
                val enProceso          = estadoMtn == "en proceso" && esElTecnico

                // ── Reporte detallado ─────────────────────────────────────────────
                var tipoFalla       = ""
                var prioridad       = ""
                var reporterNombre  = ""
                var reporterCorreo  = ""
                var descripcion     = ""
                var fechaRaw: Any?  = null

                if (reporteId > 0L) {
                    val rd = ReporteRepository.findById(reporteId)
                    if (rd != null) {
                        tipoFalla      = (rd["tipoFalla"].asMap())?.string("nombre") ?: rd.string("tipoFalla") ?: ""
                        prioridad      = (rd["prioridad"].asMap())?.string("nivel") ?: rd.string("prioridad") ?: ""
                        descripcion    = rd.string("descripcionFalla") ?: ""
                        fechaRaw       = rd["fechaReporte"]
                        val usuarioMap = rd["usuarioReporta"].asMap()
                        reporterNombre = (usuarioMap?.string("nombreCompleto") ?: usuarioMap?.string("nombre"))
                            ?.split(" ")?.firstOrNull() ?: ""
                        reporterCorreo = usuarioMap?.string("correo") ?: ""
                    }
                    // Fallbacks desde mtnData
                    if (tipoFalla.isEmpty())   tipoFalla   = (reporteMap?.get("tipoFalla").asMap())?.string("nombre") ?: ""
                    if (prioridad.isEmpty())   prioridad   = (reporteMap?.get("prioridad").asMap())?.string("nivel") ?: ""
                    if (descripcion.isEmpty()) descripcion = reporteMap?.string("descripcionFalla") ?: ""
                    if (fechaRaw == null)      fechaRaw    = reporteMap?.get("fechaReporte")
                    if (reporterNombre.isEmpty()) {
                        val u = reporteMap?.get("usuarioReporta").asMap()
                        reporterNombre = (u?.string("nombreCompleto") ?: u?.string("nombre"))
                            ?.split(" ")?.firstOrNull() ?: ""
                        reporterCorreo = u?.string("correo") ?: ""
                    }
                }

                // ── Foto del reportador ───────────────────────────────────────────
                val reporterFotoUrl = if (reporterCorreo.isNotBlank())
                    PerfilRepository.obtenerFotoUrl(reporterCorreo) else null

                // ── Evidencias ────────────────────────────────────────────────────
                val evidenciaUrls = if (reporteId > 0L)
                    ReporteRepository.listarImagenes(reporteId) else emptyList()

                uiState = uiState.copy(
                    isLoading            = false,
                    activoEtiqueta       = etiqueta,
                    activoNombre         = nombre,
                    activoNumeroSerie    = numeroSerie,
                    activoEstadoOperativo= estadoOp,
                    activoEstadoCustodia = estadoCus,
                    tipoActivoNombre     = tipoActivoNombre,
                    tipoActivoMarca      = tipoActivoMarca,
                    tipoActivoModelo     = tipoActivoModelo,
                    activoCosto          = costoDisplay,
                    activoDescripcion    = activoDesc,
                    empleadoResguardante = empleadoResguardante,
                    tipoFalla            = tipoFalla,
                    prioridad            = prioridad,
                    reporterNombre       = reporterNombre,
                    reporterFotoUrl      = reporterFotoUrl,
                    fechaDisplay         = formatearFecha(fechaRaw),
                    descripcion          = descripcion,
                    evidenciaUrls        = evidenciaUrls,
                    puedeAtender         = puedeAtender,
                    enProceso            = enProceso
                )
            } catch (e: Exception) {
                uiState = uiState.copy(isLoading = false, errorMessage = e.localizedMessage ?: "Error al cargar datos")
            }
        }
    }

    fun atender(onSuccess: () -> Unit = {}) {
        if (_mantenimientoId == 0L) return
        viewModelScope.launch {
            uiState = uiState.copy(isAtendiendo = true, atenderError = null)
            try {
                val dto = MantenimientoDTO(
                    idReporte           = _idReporte,
                    idActivo            = _idActivo,
                    idUsuarioTecnico    = _idTecnico,
                    idUsuarioAdmin      = _idAdmin,
                    idPrioridad         = _idPrioridad,
                    tipoAsignado        = _tipoAsignado,
                    estadoMantenimiento = "En Proceso"
                )
                MantenimientoRepository.update(_mantenimientoId, dto)
                uiState = uiState.copy(isAtendiendo = false, puedeAtender = false, enProceso = true)
                onSuccess()
            } catch (e: Exception) {
                uiState = uiState.copy(isAtendiendo = false, atenderError = e.localizedMessage ?: "Error de red")
            }
        }
    }

    fun clearAtenderError() { uiState = uiState.copy(atenderError = null) }

    private fun formatearFecha(raw: Any?): String {
        if (raw == null) return ""
        val date = parsearFecha(raw) ?: return raw.toString()
        val diffMs = Date().time - date.time
        val dias  = TimeUnit.MILLISECONDS.toDays(diffMs)
        val horas = TimeUnit.MILLISECONDS.toHours(diffMs)
        val relativo = when {
            dias >= 2   -> "Hace $dias días"
            dias == 1L  -> "Hace 1 día"
            horas >= 2  -> "Hace $horas horas"
            horas == 1L -> "Hace 1 hora"
            else        -> "Hace unos minutos"
        }
        val fmt = SimpleDateFormat("dd MMM, HH:mm'h'", Locale("es", "MX"))
        return "$relativo (${fmt.format(date)})"
    }

    private fun parsearFecha(raw: Any?): Date? = when (raw) {
        is List<*> -> {
            val nums = raw.filterIsInstance<Number>()
            if (nums.size >= 3) try {
                val cal = Calendar.getInstance(TimeZone.getTimeZone("UTC"))
                cal.set(Calendar.YEAR,         nums[0].toInt())
                cal.set(Calendar.MONTH,        nums[1].toInt() - 1)
                cal.set(Calendar.DAY_OF_MONTH, nums[2].toInt())
                cal.set(Calendar.HOUR_OF_DAY,  if (nums.size > 3) nums[3].toInt() else 0)
                cal.set(Calendar.MINUTE,       if (nums.size > 4) nums[4].toInt() else 0)
                cal.set(Calendar.SECOND,       if (nums.size > 5) nums[5].toInt() else 0)
                cal.set(Calendar.MILLISECOND,  0)
                cal.time
            } catch (_: Exception) { null } else null
        }
        is String  -> if (raw.isBlank()) null else {
            val utc = TimeZone.getTimeZone("UTC")
            listOf("yyyy-MM-dd'T'HH:mm:ss.SSS", "yyyy-MM-dd'T'HH:mm:ss", "yyyy-MM-dd HH:mm:ss", "yyyy-MM-dd")
                .firstNotNullOfOrNull { fmt ->
                    try { SimpleDateFormat(fmt, Locale.getDefault()).apply { timeZone = utc }.parse(raw) }
                    catch (_: Exception) { null }
                }
        }
        is Number  -> Date(raw.toLong())
        else       -> null
    }
}
