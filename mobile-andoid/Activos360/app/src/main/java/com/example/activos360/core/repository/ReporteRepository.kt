package com.example.activos360.core.repository

import android.content.Context
import android.net.Uri
import com.example.activos360.back.model.ReporteDTO
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object ReporteRepository {

    suspend fun save(dto: ReporteDTO): Long? {
        val resp = ApiProvider.reporteApi.save6(dto)
        if (!resp.isSuccessful) {
            val errorMsg = try {
                val body = resp.errorBody()?.string()
                ApiProvider.parseModelApiResponse(body ?: "")?.message
            } catch (_: Exception) { null }
            throw IllegalStateException(errorMsg ?: "Error ${resp.code()}: No se pudo registrar el reporte")
        }
        return resp.body()?.data.asMap()?.long("id")
    }

    suspend fun findById(reporteId: Long): Map<String, Any?>? {
        val resp = ApiProvider.reporteApi.findById6(reporteId)
        return if (resp.isSuccessful) resp.body()?.data.asMap() else null
    }

    suspend fun resolver(reporteId: Long) {
        val data = findById(reporteId) ?: return
        val dto = ReporteDTO(
            idActivo         = (data["activo"].asMap())?.long("id") ?: data.long("idActivo") ?: return,
            idUsuarioReporta = (data["usuarioReporta"].asMap())?.long("id") ?: data.long("idUsuarioReporta") ?: return,
            idTipoFalla      = (data["tipoFalla"].asMap())?.long("id") ?: data.long("idTipoFalla") ?: return,
            idPrioridad      = (data["prioridad"].asMap())?.long("id") ?: data.long("idPrioridad") ?: return,
            descripcionFalla = data.string("descripcionFalla") ?: "",
            estadoReporte    = "Resuelto"
        )
        try { ApiProvider.reporteApi.update6(reporteId, dto) } catch (_: Exception) { }
    }

    /**
     * Resuelve cualquier reporte abierto (no terminal) de un activo cuyo mantenimiento
     * ya esté finalizado. Se llama antes de crear un nuevo reporte para limpiar
     * reportes que quedaron "colgados" cuando el técnico cerró el mantenimiento.
     */
    suspend fun resolverAbiertos(activoId: Long) {
        try {
            val resp = ApiProvider.reporteApi.findByActivo1(activoId)
            if (!resp.isSuccessful) return
            val terminales = setOf("resuelto", "cancelado")
            val lista: List<Map<String, Any?>> = when (val data = resp.body()?.data) {
                is List<*>   -> data.filterIsInstance<Map<String, Any?>>()
                is Map<*, *> -> (data["content"] as? List<*>)
                    ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                else         -> emptyList()
            }
            for (reporte in lista) {
                val estado = (reporte["estadoReporte"] as? String)?.lowercase() ?: continue
                if (estado in terminales) continue
                val id = (reporte["id"] as? Number)?.toLong() ?: continue
                resolver(id)
            }
        } catch (_: Exception) { }
    }

    suspend fun listarImagenes(reporteId: Long): List<String> {
        fun extractUrls(data: Any?): List<String> {
            val imgs: List<Map<String, Any?>> = when (data) {
                is List<*>   -> data.filterIsInstance<Map<String, Any?>>()
                is Map<*, *> -> (data["content"] as? List<*>)
                    ?.filterIsInstance<Map<String, Any?>>() ?: emptyList()
                else         -> emptyList()
            }
            return imgs.mapNotNull { img ->
                img.string("urlCloudinary") ?: img.string("url") ?: img.string("urlImagen")
            }
        }
        // Intento principal
        try {
            val resp = ApiProvider.reporteApi.listarImagenes(reporteId)
            if (resp.isSuccessful) {
                val urls = extractUrls(resp.body()?.data)
                if (urls.isNotEmpty()) return urls
            }
        } catch (_: Exception) { }
        // Fallback
        return try {
            val resp2 = ApiProvider.imagenReporteApi.listarImagenes(reporteId)
            if (resp2.isSuccessful) extractUrls(resp2.body()?.data) else emptyList()
        } catch (_: Exception) { emptyList() }
    }

    suspend fun subirImagenes(reporteId: Long, fotos: List<Uri>, context: Context) {
        fotos.forEachIndexed { index, uri ->
            try {
                val stream = context.contentResolver.openInputStream(uri) ?: return@forEachIndexed
                val bytes = stream.readBytes()
                stream.close()
                val body = bytes.toRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part.createFormData("file", "foto_${index + 1}.jpg", body)
                ApiProvider.imagenReporteApi.subirImagen(reporteId, part)
            } catch (_: Exception) { }
        }
    }

    suspend fun loadTiposFalla(): List<Pair<Long, String>> {
        return try {
            val resp = ApiProvider.tipoFallaApi.findAll1()
            if (!resp.isSuccessful) return emptyList()
            unwrapCatalog(resp.body()?.data) { map ->
                val id     = (map["id"] as? Number)?.toLong() ?: return@unwrapCatalog null
                val nombre = map["nombre"] as? String ?: return@unwrapCatalog null
                id to nombre
            }
        } catch (_: Exception) { emptyList() }
    }

    suspend fun loadPrioridades(): List<Pair<Long, String>> {
        return try {
            val resp = ApiProvider.prioridadApi.findAll7()
            if (!resp.isSuccessful) return emptyList()
            unwrapCatalog(resp.body()?.data) { map ->
                val id    = (map["id"] as? Number)?.toLong() ?: return@unwrapCatalog null
                val nivel = map["nivel"] as? String ?: return@unwrapCatalog null
                id to nivel
            }
        } catch (_: Exception) { emptyList() }
    }

    private fun <T> unwrapCatalog(data: Any?, mapper: (Map<*, *>) -> T?): List<T> {
        val list = when (data) {
            is List<*>   -> data
            is Map<*, *> -> (data["content"] as? List<*>) ?: emptyList<Any>()
            else         -> emptyList<Any>()
        }
        return list.mapNotNull { item -> (item as? Map<*, *>)?.let(mapper) }
    }
}
