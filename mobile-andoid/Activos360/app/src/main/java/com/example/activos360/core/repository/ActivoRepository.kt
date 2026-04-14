package com.example.activos360.core.repository

import android.content.Context
import android.net.Uri
import com.example.activos360.back.model.AssetsDTO
import com.example.activos360.core.network.ApiProvider
import com.example.activos360.core.util.asMap
import com.example.activos360.core.util.long
import com.example.activos360.core.util.string
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody

object ActivoRepository {

    suspend fun findById(activoId: Long): Map<String, Any?>? {
        val resp = ApiProvider.assetsApi.findById15(activoId)
        return if (resp.isSuccessful) resp.body()?.data.asMap() else null
    }

    suspend fun isBaja(activoId: Long): Boolean {
        val activo = findById(activoId) ?: return false
        val estado = activo.string("estadoCustodia") ?: return false
        return estado.contains("baja", ignoreCase = true)
    }

    suspend fun resolveQrPayload(p: String): Long? {
        return try {
            val resp = ApiProvider.assetsApi.resolveQrPayload(p)
            if (!resp.isSuccessful) null
            else resp.body()?.data.asMap()?.long("id")
        } catch (_: Exception) { null }
    }

    suspend fun markBaja(activoId: Long, dto: AssetsDTO) {
        try { ApiProvider.assetsApi.update15(activoId, dto) } catch (_: Exception) { }
    }

    suspend fun resetEstadoOperativo(activoId: Long, nuevoEstado: String = "Disponible") {
        val activo = findById(activoId) ?: return
        val dto = AssetsDTO(
            etiqueta        = activo.string("etiqueta") ?: return,
            numeroSerie     = activo.string("numeroSerie") ?: "",
            idTipoActivo    = (activo["tipoActivo"].asMap())?.long("id") ?: activo.long("idTipoActivo") ?: return,
            idModelo        = (activo["modelo"].asMap())?.long("id") ?: activo.long("idModelo") ?: return,
            idEspacio       = (activo["espacio"].asMap())?.long("id") ?: activo.long("idEspacio") ?: return,
            estadoCustodia  = activo.string("estadoCustodia"),
            estadoOperativo = nuevoEstado,
            descripcion     = activo.string("descripcion"),
            esActivo        = true
        )
        try { ApiProvider.assetsApi.update15(activoId, dto) } catch (_: Exception) { }
    }

    suspend fun subirImagenes(activoId: Long, fotos: List<Uri>, context: Context) {
        fotos.forEachIndexed { index, uri ->
            try {
                val stream = context.contentResolver.openInputStream(uri) ?: return@forEachIndexed
                val bytes = stream.readBytes()
                stream.close()
                val body = bytes.toRequestBody("image/*".toMediaType())
                val part = MultipartBody.Part.createFormData("file", "foto_${index + 1}.jpg", body)
                ApiProvider.imagenActivoApi.subirImagen(activoId, part)
            } catch (_: Exception) { }
        }
    }
}
